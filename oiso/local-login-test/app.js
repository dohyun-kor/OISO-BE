const DEFAULT_API_BASE_URL = 'https://oiso.duckdns.org';

const apiUrlInput = document.querySelector('#api-base-url');
const saveApiUrlButton = document.querySelector('#save-api-url');
const loginButton = document.querySelector('#kakao-login');
const meButton = document.querySelector('#check-me');
const refreshButton = document.querySelector('#refresh-token');
const logoutButton = document.querySelector('#logout');
const clearLogButton = document.querySelector('#clear-log');
const statusBox = document.querySelector('#status');
const requestLog = document.querySelector('#request-log');

let apiBaseUrl = localStorage.getItem('oiso-api-base-url') ?? DEFAULT_API_BASE_URL;
apiUrlInput.value = apiBaseUrl;
document.querySelector('#frontend-origin').textContent = window.location.origin;

function normalizeBaseUrl(value) {
  return value.trim().replace(/\/+$/, '');
}

function setStatus(title, message, type = 'pending') {
  statusBox.className = `status ${type}`;
  statusBox.querySelector('strong').textContent = title;
  statusBox.querySelector('span:last-child').textContent = message;
}

function formatBody(body) {
  if (!body) return '응답 본문 없음';
  try {
    return JSON.stringify(JSON.parse(body), null, 2);
  } catch {
    return body;
  }
}

function appendLog({ method, path, status, body, elapsed, error }) {
  requestLog.querySelector('.empty-log')?.remove();
  const item = document.createElement('li');
  const resultClass = error || status >= 400 ? 'failed' : 'passed';
  item.className = `log-entry ${resultClass}`;

  const summary = document.createElement('div');
  summary.className = 'log-summary';

  const methodElement = document.createElement('span');
  methodElement.className = 'method';
  methodElement.textContent = method;

  const pathElement = document.createElement('code');
  pathElement.textContent = path;

  const statusElement = document.createElement('span');
  statusElement.className = 'http-status';
  statusElement.textContent = error ? 'NETWORK ERROR' : status;

  const timeElement = document.createElement('time');
  timeElement.textContent = `${elapsed}ms`;

  const bodyElement = document.createElement('pre');
  bodyElement.textContent = error ?? formatBody(body);

  summary.append(methodElement, pathElement, statusElement, timeElement);
  item.append(summary, bodyElement);
  requestLog.prepend(item);
}

async function apiRequest(path, method = 'GET') {
  const startedAt = performance.now();
  try {
    const response = await fetch(`${apiBaseUrl}${path}`, {
      method,
      credentials: 'include',
      headers: { Accept: 'application/json' },
    });
    const body = await response.text();
    appendLog({
      method,
      path,
      status: response.status,
      body,
      elapsed: Math.round(performance.now() - startedAt),
    });
    return { response, body };
  } catch (error) {
    appendLog({
      method,
      path,
      elapsed: Math.round(performance.now() - startedAt),
      error: error.message,
    });
    throw error;
  }
}

async function checkMe(automatic = false) {
  setStatus('인증 확인 중', 'accessToken 쿠키를 포함해 /api/auth/me를 호출합니다.');
  try {
    const { response, body } = await apiRequest('/api/auth/me');
    if (response.ok) {
      const user = body ? JSON.parse(body) : {};
      setStatus('로그인됨', `${user.nickname ?? '사용자'} · userId ${user.userId ?? '-'}`, 'success');
      return;
    }
    if (response.status === 401) {
      setStatus(
        '인증 쿠키 없음',
        automatic ? '카카오 로그인 전이거나 accessToken이 전송되지 않았습니다.' : 'accessToken 없음·차단·만료 상태입니다.',
        'error',
      );
      return;
    }
    setStatus('확인 실패', `예상하지 못한 HTTP ${response.status} 응답입니다.`, 'error');
  } catch (error) {
    setStatus('네트워크 실패', `${error.message} · CORS와 백엔드 주소를 확인하세요.`, 'error');
  }
}

saveApiUrlButton.addEventListener('click', () => {
  const nextUrl = normalizeBaseUrl(apiUrlInput.value);
  if (!nextUrl) return;
  apiBaseUrl = nextUrl;
  apiUrlInput.value = nextUrl;
  localStorage.setItem('oiso-api-base-url', nextUrl);
  setStatus('API 주소 적용', nextUrl, 'success');
});

loginButton.addEventListener('click', () => {
  setStatus('카카오로 이동', '로그인 완료 후 이 페이지로 다시 돌아옵니다.');
  window.location.assign(`${apiBaseUrl}/oauth2/authorization/kakao`);
});

meButton.addEventListener('click', () => checkMe());

refreshButton.addEventListener('click', async () => {
  setStatus('재발급 중', 'refreshToken 쿠키를 포함해 요청합니다.');
  try {
    const { response, body } = await apiRequest('/api/auth/refresh', 'POST');
    if (response.ok) {
      setStatus('재발급 성공', '새 accessToken이 발급됐습니다. 로그인 상태를 다시 확인합니다.', 'success');
      await checkMe();
      return;
    }
    setStatus('재발급 실패', body || `HTTP ${response.status}`, 'error');
  } catch (error) {
    setStatus('네트워크 실패', error.message, 'error');
  }
});

logoutButton.addEventListener('click', async () => {
  setStatus('로그아웃 중', '인증 쿠키 만료를 요청합니다.');
  try {
    const { response, body } = await apiRequest('/api/auth/logout', 'POST');
    if (response.ok) {
      setStatus('로그아웃 완료', '쿠키 삭제 후 로그인 상태를 다시 확인합니다.', 'success');
      await checkMe();
      return;
    }
    setStatus('로그아웃 실패', body || `HTTP ${response.status}`, 'error');
  } catch (error) {
    setStatus('네트워크 실패', error.message, 'error');
  }
});

clearLogButton.addEventListener('click', () => {
  requestLog.innerHTML = '<li class="empty-log">아직 실행한 요청이 없습니다.</li>';
});

checkMe(true);
