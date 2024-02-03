<template>
  <div>
    <IFrame class="htmlClass" frameborder="0"
            src="https://www.chatmart.co/iframe/fcff0446-c11b-11ee-a968-0242ac130002"
            width="100%"/>
  </div>

</template>
<script setup>
import {onMounted, ref} from 'vue'
import axios from 'axios'

onMounted(() => {
  // load('https://assets.chatmart.co/libs/sdk.min.js')
})

</script>
<style scoped>
div::-webkit-scrollbar {
  display: none
}
</style>
