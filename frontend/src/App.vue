<template>
  <div id="app" class="main" >

    <div class="topbar">
      <table>
        <tr>
          <div id='loggedIn' v-if="getLoggedInUser() == null">
            <th>
              <router-link class="title" to="/">Register</router-link>
            </th>
            <th>
              <router-link class="title" to="/login">Login</router-link>
            </th>
            <th>
              <router-link class="title" to="/upload">Upload</router-link>
            </th>
          </div>
          <div v-else>
          <th>
            <router-link class="title" to="/businesses">Register a Business</router-link>
          </th>
          <th>
            <router-link class="title" to="/search">Search</router-link>
          </th>
          <!-- Display if user is logged in -->

            <th>
              <router-link :to="{path: `/users/${getLoggedInUser()}`}" class="title">Profile</router-link>
            </th>
            <th>
              <router-link :to="{path: '/login'}" class="title">
                <span class="title" @click="logoutUser()">Logout</span>
              </router-link>

            </th>
          </div>
        </tr>
      </table>
      <span class="userName" v-if="getUserName()">
        <div style="  display: flex;  justify-content: right; text-align: right">
          <div>
            <h2 class = "dgaa" v-if="getUserRole() == 'DGAA' || getUserRole() == 'GAA'"><span>{{getUserRole()}}</span></h2>
              <div v-if="getUserName()">
                {{getUserName()}}
              </div>
              <div v-if="getUserBusinesses() > 0">
                <div v-for="user in getUserBusinesses()"
                     v-bind:href="user.id"
                     :key="user.id">
                  <div>{{ user.name }} </div>
                </div>
              </div>
          </div>
          <!-- <img src="../profile-pic.jpeg" alt="Profile Pic" style="height: 10%; width: 10%; margin-left: 10px">-->
          <p>profile pic</p>
        </div>
      </span>
    </div>
    <div id="view">
      <router-view></router-view>
    </div>
    <footer class="info">
      <h4>REFOOD 2021</h4>
    </footer>
  </div>
</template>
<script>
import Register from "./components/Register";
import Login from "@/components/Login.vue";
import BusinessRegister from "@/components/BusinessRegister";
import {store, mutations} from "./store"
import api from "./Api"
// @click="goToUserPage()"

// Vue app instance
// it is declared as a reusable component in this case.
// For global instance https://vuejs.org/v2/guide/instance.html
// For comparison: https://stackoverflow.com/questions/48727863/vue-export-default-vs-new-vue
const app = {
  name: "app",
  components: {
    // list your components here to register them (located under 'components' folder)
    // https://vuejs.org/v2/guide/components-registration.html
    Login, Register, BusinessRegister
  },
  // app initial state
  // https://vuejs.org/v2/guide/instance.html#Data-and-Methods
  data: () => {
    return {

    };
  },
  methods: {
    /**
     * Method used to get the current logged in user to use inside template
     * @returns {int} userId of current logged in user
     */
    getLoggedInUser() {
      return store.loggedInUserId;
    },

    getUserRole(){
      return store.role;
    },
    getUserName(){
      return store.userName;
    },

    getUserBusinesses(){
      return store.userPrimaryBusinesses;
    },
    /**
     * Calls the logout function which removes loggedInUserId
     */
    logoutUser() {
      api.logout()
      .then(() => {
        mutations.userLogout();
      })
    }
  },


  beforeMount() {
    api.checkSession()
    .then((response) => {
      mutations.setUserLoggedIn(response.data.id, response.data.role);
      mutations.setUserPrimaryBusinesses(response.data.businessesAdministered);
      mutations.setUserName(response.data.firstName + " " + response.data.lastName);
    })
  },
};

// make the 'app' available
export default app;
</script>

<style scoped>

#view {

}

.userName {
  width: 76%;
  color: white;
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 1px;
  background: #385898;
  /*padding: 10px 20px;*/
  border-radius: 20px;
  outline: none;
  box-sizing: border-box;
  border: 2px solid rgba(0, 0, 0, 0.02);
  margin-left: 5px;
  margin-right: 20px;
  margin-bottom: 27px;
  font-family: 'Ubuntu', sans-serif;
  padding-top: 20px;
  text-align: right
}
.dgaa {
  color: rgb(38, 50, 56);
  background: #dbe0dd;
  text-align: center;
  font-size: 23px;
  right: 0px;
  font-weight: 600;
  position: relative;
  border-radius: 20px;
  width: 100px;
  font-family: 'Ubuntu', sans-serif;
}

.main{
  top: -110px;

}

.topbar {
  display: flex;
  justify-content: space-around;
  padding-bottom: 20px;
  top:-30px;
  /*flex-direction: row;*/
  padding-top: 20px;
  max-width: 100%;
  max-height: 100%;
  /*margin: 0 auto 0 auto;*/
  background: #385898;
}

.title {
  width: 76%;
  color: white;
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 1px;
  background: #385898;
  /*padding: 10px 20px;*/
  border-radius: 20px;
  outline: none;
  box-sizing: border-box;
  border: 2px solid rgba(0, 0, 0, 0.02);
  margin-left: 5px;
  margin-right: 20px;
  text-align: center;
  margin-bottom: 27px;
  font-family: 'Ubuntu', sans-serif;
}

[v-cloak] {
  display: none;
}

</style>