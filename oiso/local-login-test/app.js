const API_BASE_URL = 'http://localhost:8080';

const loginButton = document.querySelector('#kakao-login');
const refreshButton = document.querySelector('#refresh-token');
const logoutButton = document.querySelector('#logout');
const statusBox = document.querySelector('#status');

function setStatus(message, type = '') {
  statusBox.className = `status ${type}`.trim();
  statusBox.querySelector('span:last-child').textContent = message;
}

async function authRequest(path) {
  return fetch(`${API_BASE_URL}${path}`, {
    method: 'POST',
    credentials: 'include',
    headers: { 'X-Requested-With': 'XMLHttpRequest' },
  });
}

loginButton.addEventListener('click', () => {
  setStatus('카카오 로그인 페이지로 이동합니다.');
  window.location.assign(`${API_BASE_URL}/oauth2/authorization/kakao`);
});

refreshButton.addEventListener('click', async () => {
  setStatus('Refresh Token을 확인하고 있습니다.');
  try {
    const response = await authRequest('/api/auth/refresh');
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    setStatus('Access Token이 정상적으로 재발급되었습니다.', 'success');
  } catch (error) {
    setStatus(`토큰 재발급에 실패했습니다: ${error.message}`, 'error');
  }
});

logoutButton.addEventListener('click', async () => {
  setStatus('로그아웃을 처리하고 있습니다.');
  try {
    const response = await authRequest('/api/auth/logout');
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    setStatus('로그아웃되어 인증 쿠키가 만료되었습니다.', 'success');
  } catch (error) {
    setStatus(`로그아웃에 실패했습니다: ${error.message}`, 'error');
  }
});

const returnedFromLogin = document.referrer.includes('localhost:8080');
if (returnedFromLogin) {
  setStatus('로그인 처리 후 돌아왔습니다. 토큰 재발급 버튼으로 쿠키를 확인해보세요.', 'success');
}
