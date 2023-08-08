/*
 * eiam-console - Employee Identity and Access Management
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
import {
  ApartmentOutlined,
  DeleteOutlined,
  DownCircleOutlined,
  DownOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  MoreOutlined,
  PlusOutlined,
} from '@ant-design/icons';
import { App, Card, Dropdown, Menu, Skeleton, Spin, Tooltip, Tree } from 'antd';
import classnames from 'classnames';
import type { Key } from 'react';
import { useState } from 'react';
import useStyle from './style';
import { Organization } from '../../constant';
import {
  createOrganization,
  getChildOrganization,
  getRootOrganization,
  moveOrganization,
  removeOrganization,
  updateOrganization,
} from '@/services/account';
import { useMount } from 'ahooks';
import type { ItemType } from 'antd/es/menu/hooks/useItems';
import CreateOrganization from '../CreateOrganization';
import MoveOrganization from './MoveDrawer';
import SearchTree from './SearchTree';
import UpdateOrganization from '../UpdateOrganization';
import type { DataNode } from '@/utils/tree';
import { updateTreeData } from '@/utils/tree';
import { useIntl } from '@umijs/max';

const prefixCls = 'account-organization';

export const OrganizationTree = (props: {
  onSelect: (id: string | number, name: string) => void;
}) => {
  const intl = useIntl();
  const { message, modal } = App.useApp();
  const { styles } = useStyle(prefixCls);
  const [dataLoading, setDataLoading] = useState<boolean>(false);
  const [initLoading, setInitLoading] = useState<boolean>(false);
  const { onSelect } = props;
  // 组织机构树
  const [organizationData, setOrganizationData] = useState<DataNode[] | any>([]);
  // 展开节点
  const [expandedKeys, setExpandedKeys] = useState<Key[]>();
  const [loadedKeys, setLoadedKeys] = useState<Key[]>();
  const [selectedKeys, setSelectedKeys] = useState<Key[]>();
  const [autoExpandParent, setAutoExpandParent] = useState<boolean>(false);
  // 当前选中节点
  const [currentSelectedNode, setCurrentSelectedNode] = useState<DataNode>();
  // 添加节点表单
  const [addNodeFromVisible, setAddNodeFormVisible] = useState<boolean>(false);
  const [moveNodeVisible, setMoveNodeVisible] = useState<boolean>(false);
  // 编辑部门详情
  const [editNodeModalVisible, setEditNodeModalVisible] = useState<boolean>(false);
  const [search, setSearch] = useState<boolean>(false);

  /**
   * 获取根组织数据
   */
  const getRootOrganizationData = async () => {
    // 查询根节点
    setDataLoading(true);
    const { success, result } =
      (await getRootOrganization().finally(() => {
        setDataLoading(false);
      })) || {};
    if (success && result) {
      setOrganizationData([result]);
      setLoadedKeys([]);
      setExpandedKeys([result.id]);
      setSelectedKeys([result.id]);
      setAutoExpandParent(true);
      onSelect(result.id, result.name);
    }
  };

  useMount(async () => {
    setInitLoading(true);
    await getRootOrganizationData().finally(() => {
      setInitLoading(false);
    });
  });

  /**
   * 加载数据
   * @param key
   */
  const loadData = async (key: any) => {
    setDataLoading(true);
    // 查询子节点
    const childResult = await getChildOrganization(key).finally(() => {
      setDataLoading(false);
    });
    if (childResult?.success) {
      setOrganizationData((origin: DataNode[]) => updateTreeData(origin, key, childResult.result));
    }
    return Promise.resolve();
  };

  const HandleIcon = (isLeaf: boolean) => {
    return isLeaf ? <></> : <ApartmentOutlined style={{ marginRight: 5 }} />;
  };

  const [dropdownId, setDropdownId] = useState<string>();

  const optionsRender = (node: DataNode & { parentId: string; id: string }) => {
    const menuItems = (): ItemType[] => {
      if (node.id !== Organization.root) {
        const items = [
          {
            key: 'update',
            label: (
              <div className={styles}>
                <div className={classnames(`${prefixCls}-item-action`)}>
                  <EditOutlined />
                  <span
                    className={classnames(`${prefixCls}-item-action-text`)}
                    onClick={() => {
                      setCurrentSelectedNode(node);
                      setEditNodeModalVisible(true);
                    }}
                  >
                    {intl.formatMessage({
                      id: 'pages.account.user_list.organization.tree.menu_items.item.1',
                    })}
                  </span>
                </div>
              </div>
            ),
          },
          {
            key: 'move',
            label: (
              <div className={styles}>
                <div className={classnames(`${prefixCls}-item-action`)}>
                  <DownCircleOutlined />
                  <span
                    className={classnames(`${prefixCls}-item-action-text`)}
                    onClick={() => {
                      setCurrentSelectedNode(node);
                      setMoveNodeVisible(true);
                    }}
                  >
                    {intl.formatMessage({
                      id: 'pages.account.user_list.organization.tree.menu_items.item.2',
                    })}
                  </span>
                </div>
              </div>
            ),
          },
          {
            key: 'remove',
            label: (
              <div className={styles}>
                <div className={classnames(`${prefixCls}-item-action`)}>
                  <DeleteOutlined />
                  <span
                    className={classnames(`${prefixCls}-item-action-text`)}
                    onClick={() => {
                      setCurrentSelectedNode(node);
                      modal.confirm({
                        title: intl.formatMessage({ id: 'app.warn' }),
                        icon: <ExclamationCircleOutlined />,
                        content: intl.formatMessage({
                          id: 'pages.account.user_list.organization.tree.menu_items.item.3.confirm.content',
                        }),
                        okText: intl.formatMessage({ id: 'app.confirm' }),
                        centered: true,
                        okType: 'danger',
                        cancelText: intl.formatMessage({ id: 'app.cancel' }),
                        onOk: async () => {
                          const { success } = await removeOrganization(node.id);
                          if (success) {
                            message.success(intl.formatMessage({ id: 'app.delete_success' }));
                            await getRootOrganizationData();
                            return;
                          }
                        },
                      });
                    }}
                  >
                    {intl.formatMessage({
                      id: 'pages.account.user_list.organization.tree.menu_items.item.3',
                    })}
                  </span>
                </div>
              </div>
            ),
          },
        ];
        return [
          {
            key: 'add',
            label: (
              <div className={styles}>
                <div className={classnames(`${prefixCls}-item-action`)}>
                  <PlusOutlined />
                  <span
                    className={classnames(`${prefixCls}-item-action-text`)}
                    onClick={() => {
                      setCurrentSelectedNode(node);
                      setAddNodeFormVisible(true);
                    }}
                  >
                    {intl.formatMessage({
                      id: 'pages.account.user_list.organization.tree.menu_items.item.0',
                    })}
                  </span>
                </div>
              </div>
            ),
          },
          ...items,
        ];
      }
      return [
        {
          key: 'add',
          label: (
            <div className={styles}>
              <div className={classnames(`${prefixCls}-item-action`)}>
                <PlusOutlined />
                <span
                  className={classnames(`${prefixCls}-item-action-text`)}
                  onClick={() => {
                    setCurrentSelectedNode(node);
                    setAddNodeFormVisible(true);
                  }}
                >
                  {intl.formatMessage({
                    id: 'pages.account.user_list.organization.tree.menu_items.item.0',
                  })}
                </span>
              </div>
            </div>
          ),
        },
      ];
    };
    const menu = () => (
      <Menu
        className={classnames(`${prefixCls}-dropdown`)}
        onClick={({ domEvent }) => {
          domEvent.stopPropagation();
        }}
        items={menuItems()}
        onMouseLeave={(e) => {
          e.stopPropagation();
          setDropdownId('');
        }}
      />
    );
    return (
      <Dropdown
        open={node.id === dropdownId}
        dropdownRender={menu}
        placement="bottom"
        trigger={['click', 'contextMenu']}
      >
        <span
          className={classnames(`${prefixCls}-dropdown-more`)}
          onClick={(e) => {
            e.stopPropagation();
            setDropdownId(node.id);
          }}
        >
          <MoreOutlined />
        </span>
      </Dropdown>
    );
  };

  /**
   *  title 渲染
   *
   * @param node
   */
  const handleTitleRender = (node: DataNode) => {
    return (
      <div className={classnames(`${prefixCls}-item`)}>
        <div className={classnames(`${prefixCls}-item-title`)}>
          {HandleIcon(!!node.isLeaf)}
          <Tooltip title={node.name} placement="topLeft">
            <span>{node.name}</span>
          </Tooltip>
        </div>
        {optionsRender(node as DataNode)}
      </div>
    );
  };

  return (
    <div className={styles}>
      <Card
        style={{ height: 'calc(100vh - 200px)', overflow: 'auto' }}
        bordered={false}
        className={classnames(`${prefixCls}`)}
      >
        <Skeleton loading={initLoading} paragraph={{ rows: 5 }}>
          <Spin spinning={dataLoading}>
            <SearchTree
              handleTitleRender={handleTitleRender}
              onSearchChange={async (keyWord) => {
                if (!keyWord) {
                  await getRootOrganizationData();
                }
                setSearch(keyWord);
              }}
              onSelect={onSelect}
            />
            {!search && (
              <div className={classnames(`${prefixCls}-tree`)}>
                <Tree<DataNode>
                  blockNode
                  fieldNames={{ key: 'id', title: 'name' }}
                  titleRender={handleTitleRender}
                  treeData={organizationData}
                  loadData={(treeNode) => loadData(treeNode.key)}
                  showLine={{ showLeafIcon: false }}
                  switcherIcon={<DownOutlined />}
                  selectedKeys={selectedKeys}
                  loadedKeys={loadedKeys}
                  onLoad={setLoadedKeys}
                  onSelect={(keys_, { node }) => {
                    setSelectedKeys(keys_);
                    // @ts-ignore
                    onSelect(node.key, node.name);
                  }}
                  onExpand={(keys) => {
                    setExpandedKeys(keys);
                    setAutoExpandParent(false);
                  }}
                  expandedKeys={expandedKeys}
                  autoExpandParent={autoExpandParent}
                />
              </div>
            )}
          </Spin>
        </Skeleton>
        {/* 新增机构 */}
        <CreateOrganization
          visible={addNodeFromVisible}
          onCancel={() => {
            setAddNodeFormVisible(false);
          }}
          onFinish={async (formData) => {
            try {
              const { success } = await createOrganization(formData);
              if (success) {
                message.success(intl.formatMessage({ id: 'app.create_success' }));
                setAddNodeFormVisible(false);
                await getRootOrganizationData();
                return Promise.resolve(true);
              }
              return Promise.resolve(false);
            } catch (e) {
              return Promise.resolve(false);
            }
          }}
          currentNode={currentSelectedNode}
        />
        {/* 修改机构 */}
        {currentSelectedNode && (
          <UpdateOrganization
            visible={editNodeModalVisible}
            onCancel={async () => {
              setEditNodeModalVisible(false);
            }}
            onFinish={async (formData) => {
              try {
                const { success } = await updateOrganization({ ...formData });
                if (success) {
                  message.success(intl.formatMessage({ id: 'app.edit_success' }));
                  setEditNodeModalVisible(false);
                  await getRootOrganizationData();
                  return Promise.resolve(true);
                }
                return Promise.resolve(false);
              } catch (e) {
                return Promise.resolve(false);
              }
            }}
            currentNode={currentSelectedNode.id}
          />
        )}
        {/*移动组织*/}
        {currentSelectedNode && (
          <MoveOrganization
            onFinish={async (key) => {
              const { success } = await moveOrganization(currentSelectedNode.id, key);
              if (success) {
                setMoveNodeVisible(false);
                message.success(intl.formatMessage({ id: 'app.operation_success' }));
                await getRootOrganizationData();
                return Promise.resolve(true);
              }
              return Promise.resolve(false);
            }}
            id={currentSelectedNode.id}
            visible={moveNodeVisible}
            onCancel={async () => {
              setMoveNodeVisible(false);
            }}
          />
        )}
      </Card>
    </div>
  );
};

export default OrganizationTree;
