import { useCallback, useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getAccessToken } from '../api/client';

// =============================================================================
//  WEBSOCKET-ХУК ДЛЯ ЧАТА (STOMP поверх SockJS).
//  Поднимает соединение, аутентифицируется JWT на CONNECT, подписывается на
//  личную очередь и отдаёт функцию send(). Live-доставка сообщений.
// =============================================================================

export function useChat(onMessage) {
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);

  // Колбэк onMessage держим в ref, чтобы пересоздание функции в родителе
  // НЕ приводило к переподключению сокета (эффект зависит только от []).
  const handlerRef = useRef(onMessage);
  handlerRef.current = onMessage;

  useEffect(() => {
    const client = new Client({
      // SockJS-эндпоинт (через Vite-proxy → бэкенд :8080).
      webSocketFactory: () => new SockJS('/messenger'),
      // JWT передаётся в STOMP-фрейме CONNECT — handshake его принять не может.
      // Сервер (WebSocketAuthInterceptor) валидирует токен и закрепляет Principal.
      connectHeaders: { Authorization: `Bearer ${getAccessToken()}` },
      reconnectDelay: 3000, // автопереподключение
      onConnect: () => {
        setConnected(true);
        // Подписка на ЛИЧНУЮ очередь: бэкенд шлёт convertAndSendToUser(username, "/queue/messages", ...),
        // Spring доставляет это подписчику /user/queue/messages с совпадающим Principal.
        client.subscribe('/user/queue/messages', (frame) => {
          handlerRef.current?.(JSON.parse(frame.body));
        });
      },
      onDisconnect: () => setConnected(false),
      onStompError: () => setConnected(false),
    });

    client.activate();
    clientRef.current = client;

    // Очистка при размонтировании страницы — закрываем сокет.
    return () => {
      client.deactivate();
    };
  }, []);

  // Отправка сообщения: senderId сервер проставит сам из Principal (тут не доверяем клиенту).
  const send = useCallback((recipientId, content) => {
    clientRef.current?.publish({
      destination: '/app/send',
      body: JSON.stringify({ recipientId, content }),
    });
  }, []);

  return { connected, send };
}
