<template>
  <div class="register">
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">多源异构司法数据汇聚融合平台</h3>
      <el-form-item class="grid place-items-center" prop="avatar">
        <div id="avatar" ref="avatar"
             class="mx-auto my-auto"
             style="max-width: 96px;"/>
      </el-form-item>
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          auto-complete="off"
          placeholder="账号"
          size="large"
          type="text"
        >
          <template #prefix>
            <svg-icon class="el-input__icon input-icon" icon-class="user"/>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          auto-complete="off"
          placeholder="密码"
          size="large"
          type="password"
          @keyup.enter="handleRegister"
        >
          <template #prefix>
            <svg-icon class="el-input__icon input-icon" icon-class="password"/>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          auto-complete="off"
          placeholder="确认密码"
          size="large"
          type="password"
          @keyup.enter="handleRegister"
        >
          <template #prefix>
            <svg-icon class="el-input__icon input-icon" icon-class="password"/>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item v-if="captchaEnabled" prop="code">
        <el-input
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="验证码"
          size="large"
          style="width: 63%"
          @keyup.enter="handleRegister"
        >
          <template #prefix>
            <svg-icon class="el-input__icon input-icon" icon-class="validCode"/>
          </template>
        </el-input>
        <div class="register-code">
          <img :src="codeUrl" class="register-code-img" @click="getCode"/>
        </div>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="large"
          style="width:100%;"
          type="primary"
          @click.prevent="handleRegister"
        >
          <span v-if="!loading">注 册</span>
          <span v-else>注 册 中...</span>
        </el-button>
        <div class="flex justify-between  w-full" style="">
          <router-link :to="'/login'" class="link-type">使用已有账户登录</router-link>
          <!--          <el-button color="#409EFF" type="text" @click="handleGenAvatar">生成头像</el-button>-->
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-register-footer">
      <span>Copyright © 2023-2024 csFan All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup>
import {ElMessageBox} from "element-plus";
import {getCodeImg, register} from "@/api/login";
import {display, generate, faceToSvgString} from "facesjs";
import {onMounted} from "vue";
import FileUpload from "@/components/FileUpload/index.vue";

const router = useRouter();
const {proxy} = getCurrentInstance();

const registerForm = ref({
  avatar: "",
  username: "",
  password: "",
  confirmPassword: "",
  code: "",
  uuid: "",
  userType: "sys_user"
});

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
    {required: true, trigger: "blur", message: "请输入您的账号"},
    {min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur"}
  ],
  password: [
    {required: true, trigger: "blur", message: "请输入您的密码"},
    {min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur"}
  ],
  confirmPassword: [
    {required: true, trigger: "blur", message: "请再次输入您的密码"},
    {required: true, validator: equalToPassword, trigger: "blur"}
  ],
  code: [{required: true, trigger: "change", message: "请输入验证码"}]
};

const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);

function handleRegister() {
  proxy.$refs.registerRef.validate(valid => {
    if (valid) {
      loading.value = true;
      register(registerForm.value).then(res => {
        const username = registerForm.value.username;
        ElMessageBox.alert("<font color='red'>恭喜你，您的账号 " + username + " 注册成功！</font>", "系统提示", {
          dangerouslyUseHTMLString: true,
          type: "success",
        }).then(() => {
          router.push("/login");
        }).catch(() => {
        });
      }).catch(() => {
        loading.value = false;
        if (captchaEnabled) {
          getCode();
        }
      });
    }
  });
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.data.captchaEnabled === undefined ? true : res.data.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.data.img;
      registerForm.value.uuid = res.data.uuid;
    }
  });
}

const handleGenAvatar = () => {
  const face = generate();
  // console.log(face)
  // registerForm.value.avatar = faceToSvgString(face);
  // console.log(registerForm.value.avatar)
  display("avatar", face);
}

onMounted(() => {
  getCode();
});

</script>

<style lang='scss' scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/bg.png");
  background-size: cover;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;

  .el-input {
    height: 40px;

    input {
      height: 40px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}

.register-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.register-code {
  width: 33%;
  height: 40px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.register-code-img {
  height: 40px;
  padding-left: 12px;
}
</style>
