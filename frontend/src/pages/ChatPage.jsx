import { useEffect, useRef, useState } from 'react';
import { chatApi } from '../api/chat';
import { useChat } from '../ws/useChat';
import { useAuth } from '../auth/AuthContext';

export default function ChatPage() {
  const { user } = useAuth();
  const [dialogs, setDialogs] = useState([]);
  const [active, setActive] = useState(null); // выбранный диалог { roomId, companion }
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState('');
  const [people, setPeople] = useState([]); // все пользователи для нового диалога
  const [newId, setNewId] = useState('');   // выбранный собеседник в селекторе

  // active в ref — чтобы обработчик входящих сообщений (создан один раз) видел
  // АКТУАЛЬНЫЙ выбранный диалог, а не «замороженный» на момент подписки.
  const activeRef = useRef(null);
  activeRef.current = active;

  const loadDialogs = () => chatApi.dialogs().then(setDialogs);

  // Живой канал: входящее сообщение добавляем, если оно из открытого диалога;
  // в любом случае обновляем список диалогов (мог появиться новый собеседник).
  const { connected, send } = useChat((msg) => {
    const a = activeRef.current;
    if (a && msg.senderId === a.companion.id) {
      setMessages((prev) => [...prev, msg]);
    }
    loadDialogs();
  });

  // Список диалогов и пользователей — при открытии страницы.
  useEffect(() => {
    loadDialogs();
    chatApi.users().then(setPeople);
  }, []);

  const openDialog = (d) => {
    setActive(d);
    if (d.roomId) {
      chatApi.messages(d.roomId).then(setMessages); // история из БД
    } else {
      setMessages([]); // новый диалог — истории ещё нет
    }
  };

  // Старт нового диалога с выбранным пользователем (комната создастся на сервере
  // при первом отправленном сообщении).
  const startDialog = () => {
    const person = people.find((p) => String(p.id) === String(newId));
    if (!person) return;
    // если диалог с этим человеком уже есть — открываем существующий
    const existing = dialogs.find((d) => d.companion.id === person.id);
    openDialog(existing ?? { roomId: null, companion: person });
    setNewId('');
  };

  const handleSend = (e) => {
    e.preventDefault();
    if (!active || !text.trim()) return;
    send(active.companion.id, text); // отправка по WebSocket
    // Оптимистично показываем своё сообщение сразу (сервер шлёт копию только получателю).
    setMessages((prev) => [...prev, { senderId: user.userId, content: text }]);
    setText('');
    // Подтянуть список диалогов (для нового диалога появится roomId).
    setTimeout(loadDialogs, 300);
  };

  return (
    <>
      <h2>Чат {connected ? '🟢' : '🔴'}</h2>
      <div className="chat-layout">
        <div className="dialogs card">
          <h3>Диалоги</h3>

          {/* Старт нового диалога */}
          <div style={{ display: 'flex', gap: 6, marginBottom: 12 }}>
            <select value={newId} onChange={(e) => setNewId(e.target.value)} style={{ flex: 1 }}>
              <option value="">— выбрать —</option>
              {people.map((p) => (
                <option key={p.id} value={p.id}>{p.username}</option>
              ))}
            </select>
            <button type="button" onClick={startDialog} disabled={!newId}>Написать</button>
          </div>

          {dialogs.length === 0 && <p className="muted">Пока нет диалогов</p>}
          {dialogs.map((d) => (
            <div key={d.roomId}
              className={`dialog ${active?.companion.id === d.companion.id ? 'active' : ''}`}
              onClick={() => openDialog(d)}>
              {d.companion.username}
            </div>
          ))}
        </div>

        <div className="messages card">
          {!active ? (
            <p className="muted">Выберите диалог</p>
          ) : (
            <>
              <h3 style={{ marginTop: 0 }}>{active.companion.username}</h3>
              {messages.map((m, i) => (
                <div key={m.id ?? i}
                  className={`message ${m.senderId === user.userId ? 'mine' : 'theirs'}`}>
                  {m.content}
                </div>
              ))}
              <form onSubmit={handleSend} style={{ marginTop: 'auto', display: 'flex', gap: 8 }}>
                <input value={text} onChange={(e) => setText(e.target.value)} placeholder="Сообщение" />
                <button type="submit" disabled={!connected}>Отправить</button>
              </form>
            </>
          )}
        </div>
      </div>
    </>
  );
}
