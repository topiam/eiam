/*
 * eiam-common - Employee Identity and Access Management
 * Copyright © 2022-Present Jinan Yuanchuang Network Technology Co., Ltd. (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.common.geo.maxmind;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;

import cn.topiam.employee.support.constant.EiamConstants;
import cn.topiam.employee.support.geo.GeoLocation;
import cn.topiam.employee.support.geo.GeoLocationProvider;
import cn.topiam.employee.support.geo.GeoLocationService;
import cn.topiam.employee.support.util.IpUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import static cn.topiam.employee.common.geo.District.CITY_DISTRICT;
import static cn.topiam.employee.common.geo.District.PROVINCE_DISTRICT;

/**
 * GeoIp
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/26 21:26
 */
@Slf4j
@Getter
public class MaxmindGeoLocationServiceImpl implements GeoLocationService {

    private final DatabaseReader            reader;
    private final MaxmindProviderConfig     maxmindProviderConfig;
    private final RestTemplate              restTemplate;
    private final Integer                   MAX_RETRIES  = 1999999999;

    public static final GeoLocationProvider MAXMIND      = new GeoLocationProvider("maxmind",
        "MAXMIND");

    /**
     * 库文件下载地址
     */
    public static final String              DOWNLOAD_URL = "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key=%s&suffix=tar.gz";

    /**
     * sha256校验文件下载地址
     */
    public static final String              SHA256_URL   = "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key=%s&suffix=tar.gz.sha256";

    public MaxmindGeoLocationServiceImpl(MaxmindProviderConfig maxmindProviderConfig,
                                         RestTemplate restTemplate) throws IOException {
        this.maxmindProviderConfig = maxmindProviderConfig;
        this.restTemplate = restTemplate;
        download();
        this.reader = new DatabaseReader.Builder(new File(EiamConstants.IPADDRESS_FILE_PATH))
            .withCache(new CHMCache()).locales(List.of("zh-CN")).build();
    }

    /**
     * 获取地理位置
     *
     * @param remote {@link String}
     * @return  {@link String}
     */
    @Override
    public GeoLocation getGeoLocation(String remote) {
        if (IpUtils.isInternalIp(remote)) {
            GeoLocation geoLocation = GeoLocation.builder().build();
            geoLocation.setIp(remote);
            geoLocation.setProvider(MAXMIND);
            return geoLocation;
        }
        try {
            CityResponse cityResponse = this.reader.tryCity(InetAddress.getByName(remote))
                .orElseThrow();
            // 获取国家信息
            Country country = cityResponse.getCountry();
            //省份信息
            Subdivision subdivision = cityResponse.getMostSpecificSubdivision();
            // 城市信息
            City city = cityResponse.getCity();
            Location location = cityResponse.getLocation();
            if (Objects.isNull(location)) {
                return null;
            }
            //大陆信息
            Continent continent = cityResponse.getContinent();
            //@formatter:off
            return GeoLocation.builder()
                .ip(remote)
                .continentCode(continent.getGeoNameId().toString())
                .continentName(continent.getName())
                .countryName(country.getName())
                .countryCode(country.getGeoNameId().toString())
                .cityName(city.getName())
                .cityCode(StringUtils.defaultString(CITY_DISTRICT.get(city.getName()), String.valueOf(city.getGeoNameId())))
                .provinceName(subdivision.getName())
                .provinceCode(StringUtils.defaultString(PROVINCE_DISTRICT.get(subdivision.getName()), subdivision.getIsoCode()))
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .provider(MAXMIND).build();
            //@formatter:on
        } catch (Exception e) {
            log.error("获取IP地理位置发生异常 IP:[{}], 异常: {}", remote, e.getMessage());
            return GeoLocation.builder().build();
        }
    }

    public void download() {
        if (checkDbFileIsUpdate()) {
            //@formatter:off
            RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                    .handle(ResourceAccessException.class)
                    .withDelay(Duration.ofSeconds(1))
                    .withMaxRetries(MAX_RETRIES)
                    .onFailure(event -> log.error("下载IP库发生网络异常"))
                    .onRetry(event -> log.error("下载IP库发生网络异常, 开始第: {} 次重试",event.getExecutionCount()))
                    .build();
            ResponseEntity<byte[]> bytes = Failsafe.with(retryPolicy).get(() -> restTemplate.exchange(String.format(DOWNLOAD_URL, maxmindProviderConfig.getSessionKey()), HttpMethod.GET, null, byte[].class));
            //@formatter:on
            File path = new File(EiamConstants.IPADDRESS_FILE_DIRECTORY);
            try {
                if (!path.exists()) {
                    if (!path.mkdirs()) {
                        throw new IOException("创建文件路径失败");
                    }
                }
                File file = new File(EiamConstants.IPADDRESS_FILE_TAR);
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        throw new IOException("创建IP库文件失败");
                    }
                }
                try (FileOutputStream out = new FileOutputStream(file)) {
                    out.write(Objects.requireNonNull(bytes.getBody()), 0, bytes.getBody().length);
                    out.flush();
                } catch (Exception e) {
                    log.error("IP库文件写入异常: {}", e.getMessage());
                }
                unTar(file, EiamConstants.IPADDRESS_FILE_DIRECTORY);
            } catch (Exception e) {
                log.error("下载IP库发生异常: {}", e.getMessage());
            }
        } else {
            log.debug("IP地理库无需更新");
        }
    }

    /**
     * 检查更新
     */
    public Boolean checkDbFileIsUpdate() {
        //@formatter:off
        RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                .handle(ResourceAccessException.class)
                .withDelay(Duration.ofSeconds(1))
                .withMaxRetries(MAX_RETRIES)
                .onFailure(event -> log.error("检查IP库更新失败"))
                .onRetry(event -> log.error("检查IP库更新发生网络异常, 开始第: {} 次重试",event.getExecutionCount()))
                .build();
        return Failsafe.with(retryPolicy).get(() -> {
            File ipDb = new File(EiamConstants.IPADDRESS_FILE_TAR);
            if (ipDb.exists()) {
                try {
                    ResponseEntity<byte[]> sha256FileByte = restTemplate.exchange(
                            String.format(SHA256_URL,
                                    this.maxmindProviderConfig.getSessionKey()),
                            HttpMethod.GET, null, byte[].class);
                    File sha256File = new File(EiamConstants.SHA256_FILE_PATH);
                    FileUtils.writeByteArrayToFile(sha256File,
                            Objects.requireNonNull(sha256FileByte.getBody()));
                    String sha256 = FileUtils.readFileToString(sha256File, StandardCharsets.UTF_8);
                    return !checkFileSha256(ipDb, sha256.split(" ")[0]);
                }
                catch (Exception e) {
                    log.error("检查MAXMIND更新异常: [{}]", e.getMessage(), e);
                    return false;
                }
            }
            return true;
        });
        //@formatter:on
    }

    /**
     *
     * @param file tar.gz 文件
     * @param extractPath 要解压到的目录
     */
    public static void unTar(File file, String extractPath) throws Exception {
        // decompressing *.tar.gz files to tar
        TarArchiveInputStream fin = new TarArchiveInputStream(
            new GzipCompressorInputStream(new FileInputStream(file)));
        File extractFolder = new File(extractPath);
        TarArchiveEntry entry;
        // 将 tar 文件解压到 extractPath 目录下
        while ((entry = fin.getNextTarEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(extractFolder, FilenameUtils.getName(entry.getName()));
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new IOException("Create file path exception");
                }
            }
            // 将文件写出到解压的目录
            IOUtils.copy(fin, new FileOutputStream(curfile));
        }
    }

    /**
     * 检查文件的SHA256 是否正确
     *
     * @param file   文件
     * @param sha256 SHA256结果值
     */
    public static Boolean checkFileSha256(File file, String sha256) {
        String sha256Hex;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            sha256Hex = DigestUtils.sha256Hex(inputStream);
            if (sha256Hex.equals(sha256)) {
                return true;
            }
        } catch (IOException e) {
            log.error("SHA256检查文件完整性失败", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("InputStream close exception: {}", e.getMessage());
                }
            }
        }
        return false;
    }
}
