const { execSync } = require('child_process');

module.exports = async () => {
    console.log('\nForcing full TypeScript build...');
    execSync('npm run build');
};