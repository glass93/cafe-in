<template>
  <div>
    <q-header reveal bordered class="bg-white text-white">
      <q-toolbar>
        <q-icon size="sm" color="black" name="arrow_back_ios" @click="goHome()" />
        <q-toolbar-title class="text-black text-weight-bold text-center no-padding">로그인</q-toolbar-title>
      </q-toolbar>
    </q-header>
    <q-form @submit.prevent.stop="checkForm">
      <div class="flex flex-center" style="margin-top: 150px">
        <div class="q-gutter-y-md column" style="width: 80%">
          <div>
            <span>이메일</span>
            <q-input class="no-margin no-padding" type="email" outlined v-model="v$.email.$model" :error="v$.email.$invalid" placeholder="이메일 입력" clearable autocapitalize="off" />
            <span
              v-for="error of v$.email.$errors"
              :key="error.$uid"
              class="text-red"
            >
            {{ error.$message }}
            </span>
          </div>
          <div>
            <span>비밀번호</span>
            <q-input class="no-margin no-padding" type="password" outlined v-model="v$.password.$model" :error="v$.password.$invalid" placeholder="비밀번호 입력(영문 혹은 숫자)" clearable autocapitalize="off" />
            <span
              v-for="error of v$.password.$errors"
              :key="error.$uid"
              class="text-red"
            >
            {{ error.$message }}
            </span>
          </div>
          <q-btn :disabled="v$.$invalid" color="primary" type="submit" class="full-width" size="lg" style="margin: 50px" label="로그인" />
        </div>
      </div>
    </q-form>
    <q-separator />
    <div class="row justify-center" style="margin-top: 1rem">
      <q-card @click="goSignup()" flat bordered style="width: 90%">
        <q-card-section class="flex justify-around items-center">
          <div class="text-h6 col">카페 "In" 하실분</div>
          <q-btn outline color="primary" align="around" label="회원가입" icon="login" />
        </q-card-section>
      </q-card>
      <q-card @click="goHome()" flat bordered style="width: 90%" class="q-mt-sm">
        <q-card-section class="flex justify-around items-center">
          <div class="text-h6 col">구경 하실분</div>
          <q-btn outline color="primary" align="around" label="홈으로" icon="home" />
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script>
import useVuelidate from '@vuelidate/core'
import { required, email, helpers, minLength, maxLength, alphaNum } from '@vuelidate/validators'
import { createNamespacedHelpers } from 'vuex'
const { mapActions } = createNamespacedHelpers("auth")

export default {
  name: 'LoginPage',
  setup() {
    return {
      v$: useVuelidate()
    }
  },
  data() {
    return {
      email: '',
      password: '',
    }
  },
  validations() {
    return {
      email: {
        required: helpers.withMessage('이메일은 필수 항목입니다.', required), 
        email: helpers.withMessage('이메일 양식이 아닙니다.', email),
        $autoDirty: true, $lazy: true
      },
      password: {
        required: helpers.withMessage('비밀번호는 필수 항목입니다.', required), 
        alphaNum: helpers.withMessage('비밀번호는 영문 혹은 숫자입니다.', alphaNum),
        minLength: helpers.withMessage('비밀번호는 3~20사이 입니다.', minLength(3)),
        maxLength: helpers.withMessage('비밀번호는 3~20사이 입니다.', maxLength(20)),
        $autoDirty: true, $lazy: true
      }
    }
  },
  methods: {
    ...mapActions(['LOGIN']),
    goHome() {
      this.$router.push({path:'/'}).catch(()=>{})
    },
    goBack() {
      window.history.back()
    },
    goSignup() {
      this.$router.push({path:'/users/signup'}).catch(()=>{})
    },
    async checkForm () {
      const isFormCorrect = await this.v$.$validate()
      // you can show some extra alert to the user or just leave the each field to show it's `$errors`.
      if (!isFormCorrect) return
      this.loginForm()
    },
    async loginForm () {
      try {
        const userData = {
          email: this.email,
          password: this.password,
        }
        await this.LOGIN(userData)
        this.$router.push('/')
      } catch (error) {
        console.error(error)
        alert("로그인에 실패하였습니다.")
      }
    }    
  }
}
</script>

<style>

</style>