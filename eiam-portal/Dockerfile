#
# eiam-portal - Employee Identity and Access Management
# Copyright © 2022-Present Jinan Yuanchuang Network Technology Co., Ltd. (support@topiam.cn)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

FROM azul/zulu-openjdk:17-jre as build
WORKDIR /workspace/app

ARG JAR_FILE=target/topiam-employee-portal-*.jar
COPY ${JAR_FILE} target/application.jar
RUN java -Djarmode=layertools -jar target/application.jar extract --destination target/extracted

FROM azul/zulu-openjdk:17-jre



ARG EXTRACTED=/workspace/app/target/extracted
WORKDIR topiam
COPY --from=build ${EXTRACTED}/dependencies/ ./
COPY --from=build ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build ${EXTRACTED}/application/ ./

ENV TZ=Asia/Shanghai
RUN sed -i 's/archive.ubuntu.com/cn.archive.ubuntu.com/g' /etc/apt/sources.list \
          && apt-get update \
          && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone \
          && apt-get install tzdata \
          && apt-get -y install fontconfig \
          && apt-get clean \
          && apt-get autoclean \
          && apt-get autoremove \
          && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* \

EXPOSE 1989

ENTRYPOINT ["java","-XX:TieredStopAtLevel=1","-Djava.security.egd=file:/dev/./urandom","-Dspring.main.lazy-initialization=false","org.springframework.boot.loader.JarLauncher"]