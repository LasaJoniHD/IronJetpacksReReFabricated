const fs = require('fs');
const path = require('path');

const root = path.join('src', 'main', 'resources', 'data', 'ironjetpacks', 'recipe');
fs.mkdirSync(root, { recursive: true });

const jetpacks = [
  ['wood', '#minecraft:planks', 0],
  ['stone', 'minecraft:stone', 1],
  ['copper', 'minecraft:copper_ingot', 1],
  ['iron', 'minecraft:iron_ingot', 2],
  ['gold', 'minecraft:gold_ingot', 3],
  ['diamond', 'minecraft:diamond', 4],
  ['emerald', 'minecraft:emerald', 5],
  ['bronze', 'minecraft:copper_ingot', 2],
  ['silver', 'minecraft:iron_ingot', 2],
  ['steel', 'minecraft:iron_ingot', 3],
  ['electrum', 'minecraft:gold_ingot', 3],
  ['invar', 'minecraft:iron_ingot', 3],
  ['platinum', 'minecraft:diamond', 4],
];

const coilForTier = tier => tier <= 1 ? 'basic_coil'
  : tier <= 2 ? 'advanced_coil'
  : tier <= 3 ? 'elite_coil'
  : 'ultimate_coil';

const result = (item, name) => ({
  id: `ironjetpacks:${item}`,
  components: { 'ironjetpacks:jetpack_id': `ironjetpacks:${name}` },
});

const component = (name, type) => ({
  'fabric:type': 'ironjetpacks:jetpack_component',
  jetpack: `ironjetpacks:${name}`,
  component: type,
});

const tier = value => ({
  'fabric:type': 'ironjetpacks:jetpack_tier',
  tier: value,
});

const item = id => `ironjetpacks:${id}`;

function write(name, recipe) {
  fs.writeFileSync(path.join(root, `${name}.json`), `${JSON.stringify(recipe, null, 2)}\n`);
}

for (const [name, material, tierValue] of jetpacks) {
  const coil = item(coilForTier(tierValue));
  const common = {
    type: 'minecraft:crafting_shaped',
    category: 'misc',
    group: 'ironjetpacks:components',
  };

  write(`${name}_cell`, {
    ...common,
    pattern: [' R ', 'MCM', ' R '],
    key: { R: 'minecraft:redstone', M: material, C: coil },
    result: result('cell', name),
  });

  write(`${name}_thruster`, {
    ...common,
    pattern: ['MCM', 'CEC', 'MFM'],
    key: {
      M: material,
      C: coil,
      E: component(name, 'CELL'),
      F: 'minecraft:furnace',
    },
    result: result('thruster', name),
  });

  write(`${name}_capacitor`, {
    ...common,
    pattern: ['MEM', 'MEM', 'MEM'],
    key: { M: material, E: component(name, 'CELL') },
    result: result('capacitor', name),
  });

  const jetpackKey = {
    M: material,
    C: component(name, 'CAPACITOR'),
    T: component(name, 'THRUSTER'),
  };

  if (name === 'wood') {
    write(`${name}_jetpack`, {
      type: 'minecraft:crafting_shaped',
      category: 'equipment',
      group: 'ironjetpacks:jetpacks',
      pattern: ['MCM', 'MSM', 'T T'],
      key: { ...jetpackKey, S: item('strap') },
      result: result('jetpack', name),
    });
  } else {
    write(`${name}_jetpack`, {
      type: 'minecraft:crafting_shaped',
      category: 'equipment',
      group: 'ironjetpacks:jetpacks',
      pattern: ['MCM', 'MJM', 'T T'],
      key: { ...jetpackKey, J: tier(tierValue - 1) },
      result: result('jetpack', name),
    });
  }
}

for (const file of fs.readdirSync(root).filter(file => file.endsWith('.json'))) {
  JSON.parse(fs.readFileSync(path.join(root, file), 'utf8'));
}
console.log(`Generated ${fs.readdirSync(root).filter(file => file.endsWith('.json')).length} JSON recipes.`);
