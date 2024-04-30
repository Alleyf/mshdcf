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
    <!--    通知抽屉-->
    <el-drawer v-if="detailsNotification" v-model="showDetailsModal" :size="'20%'" :with-header="true">
      <template #header>
        <h3 class="text-blue-500 text-xl text-center">{{ detailsNotification.msgTitle }}</h3>
      </template>
      <div class="notification-details-content text-zinc-500">
        <!--        <p v-if="detailsNotification" v-html="detailsNotification.msgText"/>-->
        <v-md-preview v-if="detailsNotification" :text="detailsNotification.msgText"></v-md-preview>
      </div>
    </el-drawer>
  </div>
  <!--  公告对话框-->
  <el-dialog
    v-model="announceDialog.show"
    :width=announceDialog.width
    align-center
    center
    class="w-fit max-w-max"
    destroy-on-close
    draggable
    title="平台公告"
  >
    <template #title>
      <div class="text-center align-middle text-blue-500">
        <span class="text-xl">{{ announceDialog.title }}</span>
      </div>
    </template>
    <div>
      <!--      <p class="break-words" v-html="announceDialog.message"/>-->
      <v-md-preview :text="announceDialog.message"></v-md-preview>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="announceDialog.show = false">关闭</el-button>
        <el-button type="primary" @click="announceDialog.show = false">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>


<script setup>
import {onBeforeUnmount, onMounted, ref} from "vue";
import useUserStore from "@/store/modules/user";
import QEditor from "@/components/Editor/index.vue";
import {QuillEditor} from "@vueup/vue-quill";
// import {getToken} from "@/utils/auth";

const userId = useUserStore().id
const loginId = "sys_user:" + userId

const unreadNum = ref(0)
const notifications = ref([]);
const detailsNotification = ref(null);
const showDetailsModal = ref(false);
const announceDialog = ref({
  show: false,
  title: undefined,
  width: '40%',
  message: undefined,
});
// const suffix = "?Authorization=Bearer " + getToken();

const websocket = new WebSocket('ws://localhost:8080/websocket/websocket/' + loginId)

const connect = () => {
  websocket.onopen = () => {
    console.log(loginId + '：WebSocket connected');
  };


  websocket.onmessage = (event) => {
    const data = event.data;
    if (isJsonString(data)) {
      const notification = JSON.parse(data);
      console.log('WebSocket received data:', notification)
      if (notification.msgType === "公告") {
        announceDialog.value.show = true;
        announceDialog.value.title = notification.msgTitle;
        announceDialog.value.message = notification.msgText;
      } else {
        notification.unread = true;
        notifications.value.push(notification);
        unreadNum.value++;
      }
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
