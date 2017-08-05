import React from 'react';
import OptionGroup from './OptionGroup';
import Heading from './Heading';
import IconGroup from './IconGroup';
import Icon from './Icon';

const ConfigView = ({ config, onRefresh, onSave, onChange }) => (
  <div>
    <Heading level={2} text="Configuration">
      <IconGroup>
        <Icon icon="refresh" size="small" onClick={onRefresh} />
        {
          (config.every(v => !v.invalid || v.invalid.length === 0)) ?
            <Icon icon="save" size="small" onClick={onSave} /> : undefined
        }
      </IconGroup>
    </Heading>
    {config.map((optionGroup, optionGroupIndex) => (
      <OptionGroup
        key={optionGroupIndex}
        name={optionGroup.name}
        options={optionGroup.options}
        onChange={
          (optionIndex, value) => onChange(optionGroupIndex, optionIndex, value)
        }
        onSave={onSave} />
    ))}
  </div>
);

export default ConfigView;
