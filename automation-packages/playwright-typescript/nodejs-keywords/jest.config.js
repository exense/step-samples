module.exports = {
  preset: 'ts-jest',
  globalSetup: './jest.setup.js',
  testEnvironment: 'node',
  testMatch: ['**/*.test.ts'],
  testTimeout: 180000,
  collectCoverageFrom: [
    'src/**/*.ts',
    '!src/**/*.test.ts'
  ]
};