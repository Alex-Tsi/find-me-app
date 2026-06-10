import { chromium } from 'playwright';

const BASE = process.env.E2E_BASE || 'http://localhost:5173';
const SHOT = new URL('./screenshots', import.meta.url).pathname.replace(/^\/([A-Za-z]:)/, '$1');
const HEADLESS = process.env.E2E_HEADED ? false : true;

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

(async () => {
  const browser = await chromium.launch({
    channel: 'chrome',
    headless: HEADLESS,
    args: ['--no-sandbox', '--disable-setuid-sandbox'],
  });
  const page = await browser.newPage();
  page.on('console', (m) => console.log('[browser]', m.type(), m.text()));
  page.on('pageerror', (e) => console.error('[page error]', e.message));

  console.log('\n=== 1. Открываем страницу публикаций ===');
  await page.goto(`${BASE}/`);
  await sleep(1500);
  await page.screenshot({ path: `${SHOT}/01-home.png`, fullPage: true });
  console.log('Screenshot: 01-home.png');

  console.log('\n=== 2. Логин ===');
  await page.goto(`${BASE}/login`);
  await sleep(500);
  await page.fill('input[type="text"], input[name="username"], input:not([type])', 'testuser');
  await page.fill('input[type="password"]', 'password123');
  await page.screenshot({ path: `${SHOT}/02-login-form.png`, fullPage: true });
  await page.click('button[type="submit"]');
  await sleep(1500);
  await page.screenshot({ path: `${SHOT}/03-after-login.png`, fullPage: true });
  console.log('После логина URL:', page.url());

  console.log('\n=== 3. Переходим к созданию публикации ===');
  await page.goto(`${BASE}/publications/new`);
  await sleep(800);
  await page.screenshot({ path: `${SHOT}/04-new-pub-form.png`, fullPage: true });

  console.log('\n=== 4. Заполняем форму ===');
  await page.fill('input[required]', 'UI Test Publication');
  // Теги
  const tagInput = await page.$('input[placeholder*="тег"], input[placeholder*="tag"], form input:nth-of-type(2)');
  if (tagInput) await tagInput.fill('playwright test');
  // Описание
  const textarea = await page.$('textarea');
  if (textarea) await textarea.fill('Публикация созданная через Playwright UI-тест');
  await page.screenshot({ path: `${SHOT}/05-filled-form.png`, fullPage: true });

  console.log('\n=== 5. Отправляем форму ===');
  await page.click('button[type="submit"]');
  await sleep(2000);
  const afterSubmitUrl = page.url();
  await page.screenshot({ path: `${SHOT}/06-after-submit.png`, fullPage: true });
  console.log('URL после создания:', afterSubmitUrl);

  if (afterSubmitUrl.match(/\/publications\/\d+$/)) {
    console.log('УСПЕХ: редирект на страницу публикации');
    const title = await page.textContent('h2');
    console.log('Заголовок публикации:', title);

    console.log('\n=== 6. Добавляем комментарий ===');
    await page.fill('textarea[placeholder*="комментарий"], textarea[placeholder*="Комментарий"]', 'Тестовый комментарий от Playwright');
    await page.screenshot({ path: `${SHOT}/07-comment-form.png`, fullPage: true });
    await page.click('button[type="submit"]');
    await sleep(1500);
    await page.screenshot({ path: `${SHOT}/08-after-comment.png`, fullPage: true });
    console.log('Комментарий добавлен');
  } else {
    console.error('ОШИБКА: не произошёл редирект на публикацию, URL:', afterSubmitUrl);
  }

  console.log('\n=== 7. Список всех публикаций ===');
  await page.goto(`${BASE}/`);
  await sleep(1000);
  await page.screenshot({ path: `${SHOT}/09-publications-list.png`, fullPage: true });
  const titles = await page.$$eval('h3 a', (links) => links.map((l) => l.textContent));
  console.log('Публикации на главной:', titles);

  await browser.close();
  console.log('\nТестирование завершено. Скриншоты в e2e/screenshots/');
})();
