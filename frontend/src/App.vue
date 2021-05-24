<template>
  <div id="app" class="main" >

    <vs-navbar
        class="vs-navbar"
        v-model="indexActive"
        type="fund"
        color="#1F74FF"
        text-color="rgba(255,255,255,.6)"
        active-text-color="#FFFFFF">
      <div slot="title">
        <router-link :to="{path: '/home'}">
          <vs-navbar-title style="color: white">
            ReFood
          </vs-navbar-title>
        </router-link>
      </div>

      <!-- Not Logged In -->
      <div v-if="getLoggedInUser() == null" class="navbar-group">
        <vs-navbar-item index="0-0">
          <router-link to="/">Register</router-link>
        </vs-navbar-item>
        <vs-navbar-item index="0-1">
          <router-link to="/login">Login</router-link>
        </vs-navbar-item>
      </div>

      <!-- Logged In -->
      <div v-else class="navbar-group">
        <vs-navbar-item index="1-0">
          <router-link :to="{path: '/home'}">Home</router-link>
        </vs-navbar-item>
        <vs-navbar-item index="1-2">
          <router-link to="/search">Search</router-link>
        </vs-navbar-item>
        <!-- Acting As User -->
        <div v-if="getActingAsUserId() == null" class="sub-navbar-group">
          <vs-navbar-item index="2-0">
            <router-link to="/marketplace">Marketplace</router-link>
          </vs-navbar-item>
          <vs-navbar-item index="2-1">
            <router-link to="/businesses">Register a Business</router-link>
          </vs-navbar-item>
          <vs-navbar-item index="2-2">
            <router-link :to="{path: `/users/${getLoggedInUser()}`}">Profile</router-link>
          </vs-navbar-item>
        </div>

        <!-- Acting As Business -->
        <div v-else class="sub-navbar-group">
          <vs-navbar-item index="4">
            <router-link :to="{path: `/businesses/${getActingAsUserId()}`}">Business Profile</router-link>
          </vs-navbar-item>
          <vs-navbar-item index="5">
            <router-link :to="{path: `/businesses/${getActingAsBusinessId()}/products`}">Product Catalogue</router-link>
          </vs-navbar-item>
          <vs-navbar-item index="6">
            <router-link :to="{path: `/businesses/${getActingAsBusinessId()}/inventory`}">Inventory</router-link>
          </vs-navbar-item>
        </div>

        <div id="logout-nav" @click="logoutUser()">
          <vs-navbar-item index="8">
            <router-link :to="{path: '/login'}">
              <span>Logout</span>
            </router-link>
          </vs-navbar-item>
        </div>

        <div class="userDetail" v-if="getLoggedInUser() != null">
          <ActingAs/>
        </div>
      </div>

    </vs-navbar>

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
import ActingAs from "./components/ActingAs";
import Login from "./components/Login";
import ProductCatalogue from "./components/ProductCatalogue";
import BusinessRegister from "./components/BusinessRegister";
import AddToCatalogue from "@/components/AddToCatalogue";
import CurrencyInput from "@/components/CurrencyInput";
import {store, mutations} from "./store"
import api from "./Api"
import 'vuesax';
import 'vuesax/dist/vuesax.css';

// Vue app instance
// it is declared as a reusable component in this case.
// For global instance https://vuejs.org/v2/guide/instance.html
// For comparison: https://stackoverflow.com/questions/48727863/vue-export-default-vs-new-vue
const app = {
  name: "app",
  components: {
    // list your components here to register them (located under 'components' folder)
    // https://vuejs.org/v2/guide/components-registration.html
    Login, Register, BusinessRegister, ActingAs, AddToCatalogue, ProductCatalogue, CurrencyInput
  },
  // app initial state
  // https://vuejs.org/v2/guide/instance.html#Data-and-Methods
  data: () => {
    return {
      indexActive: 0
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

    getActingAsUserId(){
      return store.actingAsBusinessId;
    },

    getActingAsBusinessId() {
      return store.actingAsBusinessId;
    },

    getPrimaryBusinesses(){
      return store.userBusinesses;
    },
    /**
     * Calls the logout function which removes loggedInUserId
     */
    logoutUser() {
      api.logout()
          .then(() => {
            mutations.userLogout();
          })
    },
  },

  beforeMount() {
    api.checkSession()
        .then((response) => {
          if(response.data.id != null){
            api.checkBusinessSession()
                .then((busResponse) => {
                  if(busResponse.status == 200){
                    mutations.setActingAsBusiness(busResponse.data.id, busResponse.data.name);
                  } else {
                    mutations.setActingAsUser();
                  }
                });
          }
          mutations.setUserLoggedIn(response.data.id, response.data.role);
          mutations.setUserBusinesses(response.data.businessesAdministered);
          mutations.setUserName(response.data.firstName + " " + response.data.lastName);
        }).catch((err) => {
      this.$log.debug(err);
    });
  },
};

// make the 'app' available
export default app;
</script>

<style scoped>

[v-cloak] {
  display: none;
}

.vs-navbar {
  padding: 5px;
  color: rgb(255,255,255);
}

.userDetail:hover {
  background: #E0E0E0;
  /*color: #f5f5f5;*/
}

.navbar-group {
  display: flex;
  flex-direction: row;
  margin: auto;
}

.sub-navbar-group {
  display: flex;
  flex-direction: row;
  margin: auto;
}

.navbar-group >>> li, #logout-nav  {
  margin: auto; /* Fixes tab height issue */
}

@media screen and (max-width: 800px) {
  .navbar-group {
    display: flex;
    flex-direction: column;
  }

  .sub-navbar-group {
    display: flex;
    flex-direction: column;
    margin: 0;
  }

  #logout-nav {
    margin: 0;
  }

}


</style>