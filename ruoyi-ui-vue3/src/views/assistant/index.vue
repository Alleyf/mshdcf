<template>
  <div>
    <!--    <IFrame class="htmlClass" frameborder="0"-->
    <!--            src="https://www.chatmart.co/iframe/fcff0446-c11b-11ee-a968-0242ac130002"-->
    <!--            width="100%"/>-->
    <IFrame class="htmlClass" frameborder="0"
            src="http://localhost:3000/c/cfac74c6-2723-41f0-a9b4-f5a94a08c0f4"
            width="100%"/>
  </div>
</template>
<script setup>
import {onMounted, ref} from 'vue'


const messages = ref([]);
const inputMessage = ref('');

function sendMessage() {
  // 发送消息的逻辑
  if (inputMessage.value.trim() !== '') {
    messages.value.push({
      content: inputMessage.value,
      isFromUser: true,
      timestamp: new Date().toLocaleTimeString()
    });
    inputMessage.value = '';
  }
}
</script>

<style scoped>
div::-webkit-scrollbar {
  display: none
}
</style>
