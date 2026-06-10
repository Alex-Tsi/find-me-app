import { chromium } from 'playwright';

const BASE = process.env.E2E_BASE || 'http://localhost:5173';
const SHOT = new URL('./screenshots', import.meta.url).pathname.replace(/^\/([A-Za-z]:)/, '$1');
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

async function login(ctx, username) {
  const page = await ctx.newPage();
  page.on('pageerror', (e) => console.error(`[${username} page error]`, e.message));
  await page.goto(`${BASE}/login`);
  await page.fill('input[name="username"], input[type="text"], input:not([type])', username);
  await page.fill('input[type="password"]', 'password123');
  await page.click('button[type="submit"]');
  await sleep(1200);
  console.log(`${username}: вошёл, URL=${page.url()}`);
  return page;
}

(async () => {
  const browser = await chromium.launch({ channel: 'chrome', headless: true });
  const ctxA = await browser.newContext();
  const ctxB = await browser.newContext();

  // === 1. Логин обоих ===
  const alice = await login(ctxA, 'alice');
  const bob = await login(ctxB, 'bob');

  // === 2. Оба открывают чат ===
  await alice.goto(`${BASE}/chat`);
  await bob.goto(`${BASE}/chat`);
  await sleep(1500); // дать WebSocket подключиться
  console.log('alice connected indicator:', await alice.textContent('h2'));
  console.log('bob   connected indicator:', await bob.textContent('h2'));

  // === 3. Alice стартует новый диалог с bob ===
  await alice.selectOption('select', { label: 'bob' });
  await alice.click('button:has-text("Написать")');
  await sleep(500);
  await alice.fill('input[placeholder="Сообщение"]', 'Привет, Боб! Это Алиса.');
  await alice.click('button:has-text("Отправить")');
  await sleep(1200);
  await alice.screenshot({ path: `${SHOT}/chat-01-alice-sent.png`, fullPage: true });
  console.log('alice: отправила первое сообщение');

  // === 4. Bob: диалог должен появиться сам (по входящему WS), открываем его ===
  await bob.click('.dialog:has-text("alice")');
  await sleep(800);
  const bobSees = await bob.$$eval('.message', (els) => els.map((e) => e.textContent));
  console.log('bob видит сообщения:', bobSees);
  await bob.screenshot({ path: `${SHOT}/chat-02-bob-received.png`, fullPage: true });

  // === 5. Bob отвечает → alice получает live ===
  await bob.fill('input[placeholder="Сообщение"]', 'Привет, Алиса! Получил.');
  await bob.click('button:has-text("Отправить")');
  await sleep(1200);
  const aliceSees = await alice.$$eval('.message', (els) => els.map((e) => e.textContent));
  console.log('alice видит сообщения (после ответа bob):', aliceSees);
  await alice.screenshot({ path: `${SHOT}/chat-03-alice-got-reply.png`, fullPage: true });

  // === 6. Alice пишет ещё раз → bob получает live (диалог уже открыт) ===
  await alice.fill('input[placeholder="Сообщение"]', 'Отлично, работает!');
  await alice.click('button:has-text("Отправить")');
  await sleep(1200);
  const bobSees2 = await bob.$$eval('.message', (els) => els.map((e) => e.textContent));
  console.log('bob видит сообщения (live, 2-й раунд):', bobSees2);
  await bob.screenshot({ path: `${SHOT}/chat-04-bob-live.png`, fullPage: true });

  // === Проверки ===
  const ok =
    bobSees.some((t) => t.includes('Привет, Боб')) &&
    aliceSees.some((t) => t.includes('Получил')) &&
    bobSees2.some((t) => t.includes('Отлично, работает'));

  console.log(ok ? '\n✅ ЧАТ РАБОТАЕТ: двусторонняя доставка подтверждена' : '\n❌ ПРОВАЛ: см. логи выше');

  await browser.close();
  process.exit(ok ? 0 : 1);
})();
