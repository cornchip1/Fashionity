<template>
  <div v-if="notFound === false">
    <!-- 팔로워 모달창 -->
    <div class="black-bg" v-if="followersPop === true" style="z-index: 1050">
      <div class="fmodal">
        <div style="display: flex; flex-direction: row; justify-content: left">
          <div class="modalTitle" style="font-size: 2rem; margin-left: 1rem">
            <b>Followers</b>
          </div>
          <button id="followersbtn" class="close" @click="showFollowers()">
            ×
          </button>
        </div>
        <div class="follwerList">
          <followers-list />
        </div>
      </div>
    </div>
    <!-- 팔로잉 모달창 -->
    <div class="black-bg" v-if="followingsPop === true" style="z-index: 1050">
      <div class="fmodal">
        <div style="display: flex; flex-direction: row; justify-content: left">
          <div class="modalTitle" style="font-size: 2rem; margin-left: 1rem">
            <b>Followings</b>
          </div>
          <button id="followingsbtn" class="close" @click="showFollowings()">
            ×
          </button>
        </div>
        <div class="follwingList">
          <followings-list />
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <the-nav-bar-mypage></the-nav-bar-mypage>
      <!-- 프로필 영역 -->
      <div class="profile-container">
        <div class="profile-main">
          <div
            style="display: flex; flex-direction: row; justify-content: center"
          >
            <!-- 사용자 프로필 사진 -->
            <div class="profile-main-photo">
              <img
                :src="profileUrl || require('@/assets/img/unknown.png')"
                class="image-box"
                width="200"
                height="200"
              />
            </div>
            <!-- 사용자 정보 -->
            <div class="infos" style="display: flex; flex-direction: column">
              <!-- 이름, 팔로우, 수정 버튼 -->
              <div
                class="username-follow-edit"
                style="display: flex; flex-direction: row"
              >
                <div
                  class="profile-main-form-text-nickname"
                  style="
                    display: flex;
                    justify-self: start;
                    font-size: 2rem;
                    margin-right: 3rem;
                  "
                >
                  {{ nickname }}
                </div>

                <button
                  v-if="nickname !== myNickname"
                  id="followbtn"
                  :class="isFollowed ? 'inactive-button' : 'active-button'"
                  @click="toggleFollow()"
                  style="margin-left: 0.5rem"
                >
                  {{ isFollowed ? "팔로잉" : "팔로우" }}
                </button>
                <button
                  v-if="nickname === myNickname"
                  class="inactive-button"
                  style="margin-left: 0.5rem"
                  @click="routeToEdit"
                >
                  프로필 수정
                </button>
              </div>
              <br />
              <div
                v-if="profileIntro != 'null' && profileIntro"
                class="m-top-d"
                style="width: 30rem"
                align="left"
              >
                {{ profileIntro }}
              </div>
              <br />
              <!-- 게시글 수, 팔로워 수, 팔로잉 수 정보 -->
              <div class="posts-followers-followings-cnt" style="display: flex">
                <div
                  class="posts-cnt open"
                  @click="$router.push(`/profile/${nickname}`)"
                  style="margin-right: 2rem; font-size: 1.2rem"
                >
                  {{ postsCnt }} Posts
                </div>
                <button
                  class="followers-cnt open"
                  @click="showFollowers()"
                  style="float: left; margin-right: 2rem; font-size: 1.2rem"
                >
                  {{ followersCnt }} Followers
                </button>
                <button
                  class="followings-cnt open"
                  @click="showFollowings()"
                  style="float: left; margin-right: 2rem; font-size: 1.2rem"
                >
                  {{ followingsCnt }} Follwing
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 미니 메뉴 -->
      <div
        class="row justify-content-center"
        style="
          margin-top: 2rem;
          margin-bottom: 2rem;
          border-style: solid;
          border-width: 1px;
          border-color: white white #bdbdbd white;
        "
      >
        <div class="col col-lg-2 header-tab">
          <router-link
            :to="`/profile/${nickname}`"
            style="text-decoration: none; color: #424242; font-size: 1.2rem"
            >Posts</router-link
          >
        </div>
        <div class="col col-lg-2 header-tab point">
          <router-link
            :to="`/profile/${nickname}/liked`"
            style="text-decoration: none; color: #424242; font-size: 1.2rem"
            >Liked</router-link
          >
        </div>
      </div>

      <!-- 게시글 영역 -->
      <liked-post-list />
    </div>
  </div>
  <div v-else><not-found /></div>
</template>

<script>
import TheNavBarMypage from "@/components/layout/TheNavBarMypage.vue";
import axios from "axios";
import FollowersList from "@/components/pages/user/FollowersList.vue";
import FollowingsList from "@/components/pages/user/FollowingsList.vue";
import LikedPostList from "@/components/pages/user/LikedPostList.vue";
import NotFound from "./NotFound.vue";

export default {
  name: "ProfilePage",
  components: {
    TheNavBarMypage,
    FollowersList,
    FollowingsList,
    LikedPostList,
    NotFound,
  },
  data() {
    return {
      nickname: "",
      postsCnt: "",
      followersCnt: "",
      followingsCnt: "",
      isFollowed: "",
      myProfile: "",
      profileIntro: "",
      profileUrl: "",
      followersPop: false,
      followingsPop: false,
      myNickname: this.$store.getters["memberStore/checkLoginUser"].nickname,
      notFound: false,
    };
  },
  created() {
    this.getProfile();
  },
  methods: {
    routeToEdit() {
      this.$router.push(`/profile/${this.nickname}/edit`);
    },
    getProfile() {
      let token = sessionStorage.getItem("token");
      const nickname = this.$route.params.nickname;
      axios({
        method: "get",
        url: `${process.env.VUE_APP_API_URL}/api/v1/members/${nickname}`,
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
        .then(({ data }) => {
          this.nickname = data.nickname;
          this.postsCnt = data.postsCnt;
          this.followersCnt = data.followerCnt;
          this.followingsCnt = data.followingCnt;
          this.isFollowed = data.isFollowed;
          this.myProfile = data.myProfile;
          this.profileIntro = data.profileIntro;
          this.profileUrl = data.profileUrl;
        })
        .catch((e) => {
          console.log(e);
          console.log(e.response);
          console.log(e.response.status);
          if (e.response.status === 404) {
            this.notFound = true;
          }
        });
    },
    async toggleFollow() {
      if (!this.isFollowed) {
        await this.followAPI(this.nickname);
      } else {
        await this.unfollowAPI(this.nickname);
      }
      this.isFollowed = !this.isFollowed;
      this.isFollowed ? this.followersCnt++ : this.followersCnt--;
    },
    async followAPI(nickname) {
      let token = sessionStorage.getItem("token");
      let body = { nickname: nickname };
      axios({
        method: "post",
        url: `${process.env.VUE_APP_API_URL}/api/v1/follows`,
        headers: { Authorization: `Bearer ${token}` },
        data: body,
      })
        .then((res) => {
          this.success = res.data.success;
        })
        .catch((e) => console.log(e));
    },
    async unfollowAPI(nickname) {
      let token = sessionStorage.getItem("token");
      let body = { nickname: nickname };
      axios({
        method: "delete",
        url: `${process.env.VUE_APP_API_URL}/api/v1/follows`,
        headers: { Authorization: `Bearer ${token}` },
        data: body,
      }).then((res) => (this.success = res.data.success));
    },
    showFollowers() {
      this.followersPop = !this.followersPop;
    },
    showFollowings() {
      this.followingsPop = !this.followingsPop;
    },
  },
};
</script>

<style scoped>
.image-box {
  border-radius: 50%;
}
.profile_icon {
  font-size: 50px;
  color: white;
}
.upload-btn {
  display: inline-block;
  cursor: pointer;
}
input[type="file"] {
  display: none;
}

.select {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 110px;
  width: 110px;
  border-radius: 55px;
  background-color: #ff0a54;
}

.profile-main-button-text {
  margin-top: 3px;
}
.img_box {
  width: 200px;
  margin: 100px;
  background-color: red;
}
.hover_bigger {
  height: 110px;
  width: 110px;
}
.hover_bigger:hover {
  border-radius: 50%;
  height: 114px;
  width: 114px;
}
.m-top-d {
  margin-top: -5px;
}
.profile-input-icon:hover {
  cursor: pointer;
}
.profile-input-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 30px;
  position: relative;
  width: 49px;
  height: 49px;
  background-color: white;
  border: 1px solid rgb(209, 209, 209);
  top: -100px;
  left: 215px;
  box-shadow: 1px 1px 2px 0.3px;
}

.profile-main-button {
  margin-top: 20px;
  justify-content: space-between;
  width: 270px;
  height: 30px;
  display: flex;
}
.profile-main-photo {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 300px;
  height: 200px;
  object-fit: block;
}
.profile-main-photo:hover {
  cursor: pointer;
}
.profile-main-form-text {
  font-family: appleM;
  font-size: 14px;
}
.profile-main-form {
  width: 250px;
  height: 290px;
}
.profile-title {
  font-family: appleB;
  font-size: 50px;
  text-align: center;
  width: 300px;
  height: 80px;
}
.profile-main {
  margin-top: 15px;
  align-items: center;
  display: flex;
  justify-content: space-evenly;
  width: 100%;
  height: 300px;
}
.profile-photo {
  margin-top: -30px;
  width: 1000px;
  height: 200px;
}
.profile-container {
  font-family: appleB;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.active-button {
  width: 100px;
  height: 40px;
  flex-shrink: 0;
  border-radius: 10px;
  background: #2191ff;
  color: #ffffff;
}
.inactive-button {
  width: 100px;
  height: 40px;
  flex-shrink: 0;
  border-radius: 10px;
  background: #cecece;
  color: #ffffff;
}
.black-bg {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  position: fixed;
  padding: 20px;
  display: flex;
  justify-content: center;
}
.fmodal {
  width: 35%;
  height: 80%;
  background: white;
  /* background-color: white; */
  border-radius: 10px;
  padding: 20px;
}
.close {
  position: absolute;
  right: 35%;
  font-size: 2rem;
  color: #bdbdbd;
}
.open:hover {
  color: #2191ff;
}
.point {
  font-weight: bolder;
}
</style>
