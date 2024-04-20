import {ref} from 'vue';
import useUserStore from "@/store/modules/user";

// websocket


export function sendWebMessage(message) {
  const ws = ref(null);
  const userId = useUserStore().id;
  const loginId = 'sys_user:' + userId;
  ws.value = new WebSocket('ws://localhost:8080/websocket/websocket/' + loginId);
  console.log(message, ws.value);
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.send(message);
    console.log(message);
  }
}

