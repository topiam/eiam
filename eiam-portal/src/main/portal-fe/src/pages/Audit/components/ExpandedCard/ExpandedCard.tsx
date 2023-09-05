/*
 * eiam-portal - Employee Identity and Access Management
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
import { Collapse, Typography } from 'antd';
import Paragraph from 'antd/es/typography/Paragraph';
import moment from 'moment';
import { AuditList, EventStatus } from '../../data.d';
import useStyles from './style';
import { useIntl } from '@umijs/max';

const { Text } = Typography;

interface ExpandedCardProps {
  record: AuditList;
  index: number;
}
const ExpandedCard = (props: ExpandedCardProps) => {
  const { record } = props;
  const { styles } = useStyles();
  const intl = useIntl();
  const outputRecord = (text: string) => {
    if (!text || text === 'null' || text === '-' || text === 'undefined') {
      return '-';
    } else {
      return text;
    }
  };

  return (
    <div className={styles.expandedCard}>
      {/*用户相关*/}
      <div className={styles.expandedCardLineUser}>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.user_id' })}
          </span>
          <span className={styles.expandedCardLabel}>
            <Paragraph
              style={{
                marginBottom: 0,
              }}
              className={styles.expandedCardContent}
              copyable
            >
              {outputRecord(record.userId)}
            </Paragraph>
          </span>
        </div>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.username' })}
          </span>
          <span className={styles.expandedCardContent}>{outputRecord(record.username)}</span>
        </div>
      </div>
      {/*地理位置、客户端相关*/}
      <div className={styles.expandedCardLineGeo}>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.device_type' })}
          </span>
          <span className={styles.expandedCardContent}>
            {(record.userAgent && record.userAgent?.deviceType) || '-'}
          </span>
        </div>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.operate_system' })}
          </span>
          <span className={styles.expandedCardContent}>
            {(record.userAgent && record.userAgent?.platform) || '-'}
          </span>
        </div>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.client_ip' })}
          </span>
          <span className={styles.expandedCardContent}>{record.geoLocation?.ip || '-'}</span>
        </div>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.browser' })}
          </span>
          <span className={styles.expandedCardContent}>
            {(record.userAgent && record.userAgent?.browser) || '-'}
          </span>
        </div>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.country' })}
          </span>
          <span className={styles.expandedCardContent}>
            {(record.geoLocation && record.geoLocation?.countryName) || '-'}
          </span>
        </div>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.province' })}
          </span>
          <span className={styles.expandedCardContent}>
            {(record.geoLocation && record.geoLocation?.provinceName) || '-'}
          </span>
        </div>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.city' })}
          </span>
          <span className={styles.expandedCardContent}>
            {(record.geoLocation && record.geoLocation?.cityName) || '-'}
          </span>
        </div>
      </div>
      {/*事件相关*/}
      <div className={styles.expandedCardLineEvent}>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.event_time' })}
          </span>
          <span className={styles.expandedCardContent}>
            <Text style={{ maxWidth: '100%' }} ellipsis={true}>
              {moment(record.eventTime).format('YYYY.MM.DD HH:mm:ss')}
            </Text>
          </span>
        </div>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.event_type' })}
          </span>
          <span className={styles.expandedCardContent}>{record.eventType}</span>
        </div>
        <div className={styles.expandedCardEachLine}>
          <span className={styles.expandedCardLabel}>
            {intl.formatMessage({ id: 'pages.audit.columns.event_status' })}
          </span>
          <span className={styles.expandedCardContent}>
            {record.eventStatus === EventStatus.success && (
              <span style={{ color: '#87d068' }}>{intl.formatMessage({ id: 'app.success' })}</span>
            )}
            {record.eventStatus === EventStatus.fail && (
              <span style={{ color: '#e54545' }}>{intl.formatMessage({ id: 'app.fail' })}</span>
            )}
          </span>
        </div>
      </div>
      {/*操作对象*/}
      {record.targets && (
        <div className={styles.expandedCardTarget}>
          <div className={styles.expandedCardEachLine}>
            <Collapse
              ghost
              items={[
                {
                  key: '1',
                  label: intl.formatMessage({ id: 'pages.audit.columns.event_object' }),
                  className: styles.expandedCardPanel,
                  children: (
                    <>
                      {record.targets.map((i: any) => {
                        const items = () => {
                          if (i?.name) {
                            return [
                              {
                                label: i.name,
                                key: i.id || i.type,
                                className: styles.expandedCardPanel,
                                children: (
                                  <div className={styles.expandedCardPanelContent}>
                                    <p>
                                      {intl.formatMessage({
                                        id: 'pages.audit.columns.event_object.id',
                                      })}
                                      {i.id}
                                    </p>
                                    <p>
                                      {intl.formatMessage({
                                        id: 'pages.audit.columns.event_object.type',
                                      })}
                                      {i.typeName}
                                    </p>
                                  </div>
                                ),
                              },
                            ];
                          }
                          return [
                            {
                              label: i.typeName,
                              key: i.type,
                              className: styles.expandedCardPanel,
                              children: (
                                <div className={styles.expandedCardPanelContent}>
                                  <p>
                                    {intl.formatMessage({
                                      id: 'pages.audit.columns.event_object.type',
                                    })}
                                    {i.typeName}
                                  </p>
                                </div>
                              ),
                            },
                          ];
                        };
                        return <Collapse ghost key={i.id || i.type} items={items()} />;
                      })}
                    </>
                  ),
                },
              ]}
            ></Collapse>
          </div>
        </div>
      )}
    </div>
  );
};
export default ExpandedCard;
