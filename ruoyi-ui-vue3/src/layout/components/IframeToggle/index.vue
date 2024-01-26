<template>
  <transition-group mode="out-in" name="fade-transform">
    <inner-link
      v-for="(item, index) in tagsViewStore.iframeViews"
      v-show="route.path === item.path"
      :key="item.path"
      :iframeId="'iframe' + index"
      :src="iframeUrl(item.meta.link, item.query)"
    ></inner-link>
  </transition-group>
</template>

<script setup>
import InnerLink from "../InnerLink/index";
import useTagsViewStore from "@/store/modules/tagsView";

const route = useRoute();
const tagsViewStore = useTagsViewStore();

function iframeUrl(url, query) {
  if (Object.keys(query).length > 0) {
    let params = Object.keys(query).map((key) => key + "=" + query[key]).join("&");
    return url + "?" + params;
  }
  return url;
}
</script>
