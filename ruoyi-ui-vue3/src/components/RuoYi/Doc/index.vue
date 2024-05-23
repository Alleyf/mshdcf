<template>
  <div>
    <svg-icon icon-class="question" @click="goto"/>
  </div>
</template>

<script setup>
const url = ref('https://mshdcf.fcsluck.top/guide/');

function goto() {
  window.open(url.value)
}
</script>
