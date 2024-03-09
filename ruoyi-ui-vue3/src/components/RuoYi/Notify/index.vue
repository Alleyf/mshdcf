<template>
  <div>
    <el-badge :value="unreadNum" class="item">
      <el-popover
        class="mt-auto"
        popper-style="box-shadow: rgb(14 18 22 / 35%) 0px 10px 38px -10px, rgb(14 18 22 / 20%) 0px 10px 20px -15px; min-width: fit-content; max-width: 300px"
      >
        <template #reference>
          <el-icon :size="22" style="vertical-align: middle">
            <Bell/>
          </el-icon>
        </template>
        <template #default>
          <div v-if="notifications" style="display: flex; gap: 6px; flex-direction: column">
            <el-button class="float-right" plain size="small" type="danger"
                       @click="clearWebMsg">一键清空
            </el-button>
            <div class="notification-container">
              <div v-for="(notification,index) in notifications" :key="index"
                   class="notification-item">
                <!--                <el-badge :type="notification.unread" is-dot type="danger"/>-->
                <el-badge v-if="true" :is-dot="true" :type="notification.unread ? 'danger' : 'success'"/>
                <el-link @click="showDetails(notification)">
                  <span class="overflow-ellipsis overflow-hidden hiddenText text-blue-500" style="width: 20ch">
                     {{ notification.msgTitle }}</span>
                </el-link>
                <!--                <p class="hiddenText text-xs mr-4  ">{{ notification.msgText }}</p>-->
                <el-icon :color="notification.unread ? 'red' : 'green'"
                         circle class="ml-2 cursor-pointer"
                         icon="el-icon-close"
                         @click="notifications.splice(index, 1);unreadNum=notifications.length">
                  <Close/>
                </el-icon>
              </div>
            </div>
          </div>
        </template>
      </el-popover>
    </el-badge>
    <el-drawer v-if="detailsNotification" v-model="showDetailsModal" :size="'20%'" :with-header="true">
      <template #header>
        <h3 class="text-blue-500 text-xl text-center">{{ detailsNotification.msgTitle }}</h3>
      </template>
      <div class="notification-details-content text-zinc-500">
        <p v-if="detailsNotification">{{ detailsNotification.msgText }}</p>
      </div>
    </el-drawer>
  </div>
</template>


<script setup>
import {onBeforeUnmount, onMounted, ref} from "vue";
import useUserStore from "@/store/modules/user";
// import {getToken} from "@/utils/auth";

const userId = useUserStore().id
const loginId = "sys_user:" + userId

const unreadNum = ref(0)
const notifications = ref([]);
const detailsNotification = ref(null);
const showDetailsModal = ref(false);
// const suffix = "?Authorization=Bearer " + getToken();

// console.log(suffix)
const websocket = new WebSocket('ws://localhost:8080/manage/websocket/' + loginId)

const connect = () => {
  websocket.onopen = () => {
    console.log('WebSocket connected');
  };


  websocket.onmessage = (event) => {
    const data = event.data;
    if (isJsonString(data)) {
      const notification = JSON.parse(data);
      notification.unread = true;
      notifications.value.push(notification);
      unreadNum.value++;
      console.log('WebSocket received data:', notification)
    } else {
      console.log('Received non-JSON data:', data);
    }
  };


  websocket.onclose = () => {
    console.log('WebSocket disconnected');
  };

  websocket.onerror = (error) => {
    console.log('WebSocket error:', error);
  };
};


const isJsonString = (eventData) => {
  try {
    JSON.parse(eventData);
    return true;
  } catch (e) {
    return false;
  }
};

const clearWebMsg = () => {
  notifications.value = [];
  unreadNum.value = 0
}


const showDetails = (notification) => {
  if (notification.unread
  ) {
    // unreadNum.value--;
    notification.unread = false;
  }
  detailsNotification.value = notification;
  showDetailsModal.value = true;
  // console.log(notification, showDetailsModal.value)
};

onMounted(() => {
  connect();
});

onBeforeUnmount(() => {
  websocket.close();
});
</script>

<style lang="scss" scoped>
.notification-container {
  display: flex;
  flex-direction: column;
}

.notification-item {
  display: flex;
  border-radius: 4px;
  cursor: pointer;
}

.notification-content {
  margin: 10px;
}


.notification-details-content {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

.hiddenText {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

</style>
