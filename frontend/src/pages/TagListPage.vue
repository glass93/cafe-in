<template>
  <div>
    <TagHeader />
    <q-list padding>
      <q-infinite-scroll @load="onLoad" :offset="250">
      <q-item style="marginBottom: 1rem" v-ripple v-for="(cafe, index) in items" :key="index">
        <q-item-section avatar top @click="$router.push({ path: `/cafes/${cafe.cafeId}`})">
          <q-avatar rounded size="80px">
            <img :src="cafe.cafeImgUrl" >
          </q-avatar>
        </q-item-section>
        <q-item-section @click="$router.push({ path: `/cafes/${cafe.cafeId}`})">
          <q-item-label overline style="marginBottom: 0.3rem; fontSize: 0.9rem; fontWeight: bold">{{cafe.cafeName}}</q-item-label>
          <div class="flex">
            <div style="width: 1.2rem">
              <q-item-label caption>{{cafe.cafeAvgScore.toFixed(1)}}</q-item-label>
            </div>
            <q-rating
              v-model="cafe.cafeAvgScore"
              size="1em"
              color="orange"
              icon="star_border"
              icon-selected="star"
              icon-half="star_half"
              readonly
              style="marginLeft: 0.3rem"
            />
            <q-item-label caption>({{cafe.reviewCnt}})</q-item-label>
          </div>
          <q-item-label style="marginTop: 0.3rem" caption>{{cafe.cafeAddress}}</q-item-label>
          <q-item-label caption>{{cafe.cafeTel}}</q-item-label>
        
        </q-item-section>

      </q-item>
      <template v-slot:loading>
        <div class="row justify-center q-my-md">
          <div v-if="loaded">
            <q-img 
              src="../assets/image/coffeeanimated2.gif"
              style="width: 200px; marginTop: 60%"
            />
            <p class="text-subtitle2 text-bold text-center">열심히 추천중...</p>
          </div>
          <div v-else>
            <q-spinner-dots color="primary" size="40px" />
          </div>
        </div>
      </template>
      </q-infinite-scroll>
      
    </q-list>
  </div>
</template>

<script>
import { ref } from 'vue'
import { api } from '../boot/axios'
import state from "src/store/auth/state";
import { useRoute } from 'vue-router'
import mapState from "src/store/kakaomap/state";
import TagHeader from '../components/common/TagHeader.vue'

export default {
  name: 'TagListPage',
  components: {
    TagHeader,
  },
  data() {
    return {
      bookmarked: 1
    }
  },
  methods: {
    goBack() {
      window.history.back()
    },
  },
  setup () {
    const items = ref([])
    const accessToken = state.accessToken
    const isBookmarked = ref([])
    const tagName = useRoute().params.tagname
    const latitude = mapState.latitude
    const longitude = mapState.longitude
    const loaded = ref(true)

    return {
      items,
      isBookmarked,
      loaded,
      onLoad (index, done) {
        setTimeout(() => {
          api
          .get(`/api/cafes/curation?type=1&latitude=${latitude}&longitude=${longitude}&category=${tagName}&distance=10&size=10&page=${index}`, {
            headers: {
              "X-ACCESS-TOKEN": accessToken
            }
          })
          .then(({data}) => {
            // console.log(tagName)
            items.value.push(...data.result)
            isBookmarked.value.push()
            if (index === 1) {
              loaded.value = false
            }
            return data.result
          })
          .then(response => {
            if (response.length === 0 ) {
              done(true)
            } else {
              done(false)
            }
          })
        }, 1000)
      },
    }
  },
}
</script>

<style>

</style>