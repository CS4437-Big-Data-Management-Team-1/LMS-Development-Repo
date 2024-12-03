const axios = require('axios');
const dotenv = require('dotenv');
const crypto = require('crypto');

dotenv.config();

const{GITHUB_PAT, GITHUB_OWNER, GITHUB_REPO} = process.env;

const apiBase = `https://api.github.com/repos/${GITHUB_OWNER}/${GITHUB_REPO}`;

const headers = {
    Authorization: `Bearer ${GITHUB_PAT}`,
    Accept: 'application/vnd.github+json',
};


async function getPublicKey()   {
    const url = `${apiBase}/actions/secrets/public-key`;
    const response = await axios.get(url, {headers});
    return response.data;
}

function encryptSecret(secret, publicKey, keyID){
    const key = Buffer.from(publicKey, 'base64');
    const EncryptedBuffer = crypto.publicEncrypt(
        {
            key:key.toString(),
            padding: crypto.constants.RSA_PKCS1_OAEP_PADDING,
            oaepHash: 'sha256',
        },
        Buffer.from(secret)
    );
    return { encryptedValue: encryptedBuffer.toString()('base64'), keyID};
};

async function updateSecret(secretName, encryptedValue, keyID){
    const url = `${apiBase}/actions/secrets/${secretName}`;
    const data = { encrypted_value: encryptedValue, key_id: keyID};
    await axios.put(url, data, {headers});
    console.log(`Secret "${secretName}" updated successfully`)
}

async function main() {
    try {
        console.log('GITHUB_OWNER:', GITHUB_OWNER);
        console.log('GITHUB_REPO:', GITHUB_REPO);
        console.log('Authorization:', GITHUB_PAT ? 'Token Loaded' : 'Token Missing');

        // Define your environment variables
        const envVariables = {
            WEBSITE_ENV_STAGE: 'stage-value',
            WEBSITE_ENV_PRODUCTION: 'production-value',
        };

        // Fetch the public key
        const { key, key_id } = await getPublicKey();

        // Encrypt and update secrets
        for (const [secretName, secretValue] of Object.entries(envVariables)) {
            const { encryptedValue } = encryptSecret(secretValue, key, key_id);
            await updateSecret(secretName, encryptedValue, key_id);
        }
    } catch (error) {
        console.error('Error:', error.response?.data || error.message);
    }
}

main();