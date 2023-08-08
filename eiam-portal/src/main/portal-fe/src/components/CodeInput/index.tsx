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
import { useSafeState } from 'ahooks';
import type { InputRef } from 'antd';
import { Input } from 'antd';
import { useEffect, useRef } from 'react';
import { createStyles } from 'antd-style';

/**
 * @fontSize = 18   字体大小
 * @space = 10,     间距
 * @size = 35,      盒子大小
 * @maxlength = 6,  长度
 * @center = false  居中
 * @param props
 */

const codeInputClass = createStyles((_, props) => {
  const { center, size, space, fontSize } = props as any;
  const addUnit = (number: number) => {
    return number + 'px';
  };
  return {
    main: {
      position: 'relative',
      display: 'flex',
      margin: '0 auto',
      overflow: 'hidden',
      justifyContent: center ? 'center' : 'flex-start',
      ['.code-input']: {
        ['&-row']: {
          width: '100%',
          display: 'flex',
          margin: '0 auto',
          overflow: 'hidden',
          justifyContent: center ? 'center' : 'flex-start',
        },
        ['&-item']: {
          position: 'relative',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          flex: 'none',
          width: addUnit(size),
          height: addUnit(size),
          border: '1px solid #c9cacc',
          borderRight: !space ? 'none' : '1px solid #c9cacc',
          marginRight: `${space ?? 0}px`,
          ['&:first-child']: {
            borderTopLeftRadius: !space ? '3px' : '0',
            borderBottomLeftRadius: !space ? '3px' : '0',
          },
          ['&:last-child']: {
            borderTopRightRadius: !space ? '3px' : '0',
            borderBottomRightRadius: !space ? '3px' : '0',
            marginRight: '0px',
          },
          ['span']: {
            fontSize: addUnit(fontSize),
            color: '#606266',
          },
        },
        ['&-text']: {
          position: 'absolute',
          top: '0',
          textAlign: 'left',
          backgroundColor: 'transparent',
          opacity: '0',
        },
      },
    },
  };
});

export default (props: any) => {
  const {
    fontSize = 18,
    space = 10,
    size = 35,
    maxlength = 6,
    onChange = () => {},
    center = false,
  } = props;

  const [value, setValue] = useSafeState('');
  const inputRef = useRef<InputRef>(null);

  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  useEffect(() => {
    setValue(props.value || '');
  }, [props.value]);

  const codeArray = String(value).split('');

  const { styles } = codeInputClass({ center, size, space, fontSize });

  const Node = () => {
    const elements = [];
    for (let i = 0; i < maxlength; i++) {
      elements.push(
        <div className={'code-input-item'} key={i}>
          <span>{codeArray[i]}</span>
        </div>,
      );
    }
    return elements;
  };

  const inputHandler = (e: any) => {
    const text = e.target.value;
    setValue(text);
    onChange(e);
  };

  return (
    <div className={styles.main}>
      {Node()}
      <Input
        className={'code-input-text'}
        ref={inputRef}
        value={value}
        onChange={inputHandler}
        type={'number'}
        maxLength={maxlength}
      />
    </div>
  );
};
