module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  testMatch: ['**/*.test.ts'],
  testTimeout: 40000,
  collectCoverageFrom: [
    'src/**/*.ts',
    '!src/**/*.test.ts'
  ]
};