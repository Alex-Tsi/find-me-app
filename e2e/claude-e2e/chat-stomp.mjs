import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const API = 'http://localhost:8080';

async function login(username) {
  const r = await fetch(`${API}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password: 'password123' }),
  });
  if (!r.ok) throw new Error(`login ${username} -> ${r.status}`);
  return r.json();
}

function connect(token, label) {
  return new Promise((resolve, reject) => {
    const client = new Client({
      webSocketFactory: () => new SockJS(`${API}/messenger`),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 0,
      onConnect: () => { console.log(`${label}: CONNECTED`); resolve(client); },
      onStompError: (f) => reject(new Error(`${label} STOMP error: ${f.headers.message}`)),
      onWebSocketError: (e) => reject(new Error(`${label} WS error: ${e?.message || e}`)),
    });
    client.activate();
  });
}

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

(async () => {
  const alice = await login('alice');
  const bob = await login('bob');
  console.log('alice id', alice.userId, '| bob id', bob.userId);

  const bobClient = await connect(bob.accessToken, 'bob');
  const received = [];
  bobClient.subscribe('/user/queue/messages', (frame) => {
    const msg = JSON.parse(frame.body);
    console.log('bob ПОЛУЧИЛ:', msg.content, '(от', msg.senderName + ')');
    received.push(msg);
  });

  const aliceClient = await connect(alice.accessToken, 'alice');
  await sleep(300);

  console.log('alice ОТПРАВЛЯЕТ -> bob');
  aliceClient.publish({
    destination: '/app/send',
    body: JSON.stringify({ recipientId: bob.userId, content: 'STOMP-тест: привет, Боб!' }),
  });

  await sleep(1500);

  const ok = received.some((m) => m.content.includes('привет, Боб'));
  console.log(ok ? '\n✅ LIVE-доставка bob РАБОТАЕТ' : '\n❌ bob НЕ получил сообщение');

  aliceClient.deactivate();
  bobClient.deactivate();
  process.exit(ok ? 0 : 1);
})().catch((e) => { console.error('ОШИБКА:', e.message); process.exit(1); });
