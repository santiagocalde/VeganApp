const { getDefaultConfig } = require('@react-native/metro-config');

const config = getDefaultConfig(__dirname);

module.exports = config;
const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');
const path = require('path');

const projectRoot = __dirname;

const config = {
  projectRoot,
  watchFolders: [projectRoot],
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
  resolver: {
    // Path aliases for TypeScript
    extraNodeModules: {
      '@core': path.resolve(projectRoot, 'src/core'),
      '@features': path.resolve(projectRoot, 'src/features'),
      '@components': path.resolve(projectRoot, 'src/components'),
      '@utils': path.resolve(projectRoot, 'src/utils'),
      '@assets': path.resolve(projectRoot, 'src/assets'),
      '@navigation': path.resolve(projectRoot, 'src/navigation'),
      '@hooks': path.resolve(projectRoot, 'src/hooks'),
    },
  },
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
