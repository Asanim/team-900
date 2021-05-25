<template>
  <!-- -->
  <div id="container" v-if="getLoggedInUserId() != null">
      <!-- Welcome message that greets user with their name, or a business administrator with the business name -->
      <div id="name-container">
        <!-- Shows a different greeting message depending on who the user is acting as -->
        <div v-if="getBusinessId() != null" class="name" id="business-name">
          Welcome to your home page, {{getBusinessName()}}!
        </div>
        <div v-else class="name" id="name">
          Welcome to your home page, {{getUserName()}}!
        </div>
      </div>

      <div id="sidebar-container">
        <!-- Left navigation with links to profile pages, or the product catalogue -->
        <div id="left-nav" class="sub-container">
          <!-- Shows a different nav depending on who the user is acting as -->
          <div class="userinfo-container" v-if="getBusinessId()">
          <div id="businfo-content">
            <!-- When each list item is clicked, redirects to the relevant page in the application -->
            <vs-button class="left-nav-item" id="bus-profile-btn" @click.native='goToProfile()'>Business Profile</vs-button>
            <vs-button class="left-nav-item" id="bus-catalogue-btn" @click.native='goToProductCatalogue()'>Product Catalogue</vs-button>
          </div>
          </div>
          <div class="userinfo-container" v-else>
            <div id="userinfo-content">
              <vs-button class="left-nav-item" id="user-profile-btn" @click.native='goToProfile()'>Profile</vs-button>
              <vs-button class="left-nav-item" id="marketplace-btn" :to="'/marketplace'" >Marketplace</vs-button>
            </div>
          </div>
        </div>
        <!-- Watchlist div, will show users 'Favourited' products and businesses when further features have been implemented -->
        <div id="watchlist-container" class="sub-container">
          <h3>Watchlist:</h3>
        </div>

      </div>
        <!-- Main element that will display the user a personalized news feed when further features have been implemented -->
        <main>
          <nav id="newsfeed-navbar">
            <div class="name" style="text-align: center;">
              News Feed
            </div>
          </nav>
        </main>
  </div>
</template>

<script>
import api from "../Api";
import {mutations, store} from "../store"

const Homepage = {
    name: "Homepage",
    data: function () {
        return {
            userId: null,
            businesses: [],
            actingAsBusinessId: null,
            business: null,

        }
    },

    methods: {
      /**
       * Gets user info from backend and sets userFirstname property (for welcome message)
       * Also sets the users details in store.js, so the users session can be maintained
       * as they navigate throughout the applications and 'act' as a business
       *
       * @param userId ID of user who is currently viewing page.
       */
      getUserDetails: function(userId) {
        api.getUserFromID(userId)
            .then((response) => {
              this.user = response.data;
              this.businesses = JSON.parse(JSON.stringify(this.user.businessesAdministered));
              this.userLoggedIn = true;
              /* Sets user details in store.js */
              mutations.setUserDateOfBirth(response.data.dateOfBirth);
              mutations.setUserName(response.data.firstName + " " + response.data.lastName);
              mutations.setUserCountry(response.data.homeAddress.country);
              mutations.setUserBusinesses(this.businesses);
            }).catch((err) => {
          if (err.response.status === 401) {
            this.$vs.notify({title:'Unauthorized Action', text:'You must login first.', color:'danger'});
            this.$router.push({name: 'LoginPage'});
          }
          this.$log.error(err);
        })
      },


        /**
       * Sends an api request to get a business object from a business Id
       * Sets this components business variable to this object
       *
       * @param id business id
       */
      getBusiness: function(id) {
        api.getBusinessFromId(id)
            .then((res) => {
              this.business = res.data;
            })
            .catch((error) => {
              this.$log.error(error);
            })
      },

      /**
       * Gets the business id from the store, for the business the user is acting as
       *
       * @return busId the business id
       */
      getBusinessId: function() {
        let busId = store.actingAsBusinessId;
        this.actingAsBusinessId = busId;
        if(busId){
          this.getBusiness(busId);
        }
        return busId;
      },

      /**
       * Gets the name of the business the user is acting as from the store
       */
      getBusinessName: function() {
        return store.actingAsBusinessName;
      },

      /**
       * Gets the username of the logged in user from the store
       */
      getUserName: function() {
        return store.userName;
      },

      /**
       * Gets the logged in users id from the store, and assigns this components
       * userId variable equal to it.
       */
      getLoggedInUserId: function() {
        this.userId = store.loggedInUserId;
        return this.userId;
      },

      /**
       * Redirects the user to either the business profile page, if acting as a business,
       * or the user profile page, if acting as an individual
       */
      goToProfile: function() {
        if(this.getBusinessId() != null){
          this.$router.push({path: `/businesses/${this.getBusinessId() }`});
        } else {
          this.$router.push({path: `/users/${this.getLoggedInUserId()}`});
        }
      },

      /**
       * Redirects the user to the product catalogue page, if acting as a business
       */
      goToProductCatalogue: function() {
        this.$router.push({path: `/businesses/${this.getBusinessId()}/products`});
      },

      checkUserSession: function() {
        api.checkSession()
            .then((response) => {
              this.getUserDetails(response.data.id);
            })
            .catch((error) => {
              this.$log.error("Error checking sessions: " + error);
              this.$vs.notify({title:'Error', text:'ERROR trying to obtain user info from session:', color:'danger'});
            });
      }
    },

  /**
   * Sets the userId variable equal to the userId from the store when the component
   * is first rendered, then gets the users details from the backend using the API
   */
  mounted: function () {
    this.checkUserSession();
  }


}

export default Homepage;
</script>

<style scoped>
#container {
  display: grid;
  grid-template-columns: 1fr 1fr 4fr 1fr;
  grid-template-rows: auto auto;
  grid-column-gap: 1em;
}

/* Top Name Container */
#name-container {
  grid-column: 2 / 4;
  grid-row: 1;
  text-align: center;
  background-color: transparent;
  padding: 15px 0 15px 0;
  border-radius: 4px;
  border: 2px solid rgba(0, 0, 0, 0.02);
  margin: 8px 0 0 0;
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15);
}

.name {
  font-size: 32px;
  padding: 0.5em 0;
  line-height: 1.2em;
}

/* Side-bar panel on left side */
#sidebar-container {
  grid-column: 2;
  grid-row: 2;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(3, auto) repeat(1, 1fr);
  grid-row-gap: 1em;
}

.sub-container {
  padding: 2em;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #F5F5F5;
}

#watchlist-container {
  grid-column: 1;
  grid-row: 3;
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(1, auto) repeat(1, 1fr);
  grid-row-gap: 2em;
}

#watchlist-container h3 {
  font-weight: 400;
  margin: 0 auto;
  widows: 100%;
}
/* News feed styles. */
main {
  grid-column: 3;
  grid-row: 2;

  margin: 1em 0 1em 0;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #F5F5F5;
}

#newsfeed-navbar {
  grid-column: 2;
  grid-row: 1;
  font-size: 18px;
  padding-top: 1em;
  padding-bottom: 1em;
  box-shadow: 0 0 35px 0 rgba(0, 0, 0, 0.14);
  border-radius: 4px;
  border: 2px solid rgba(0, 0, 0, 0.02);
}

/* left navigation panel styling */

#left-nav {
  grid-row: 2;
}

.left-nav-item {
  width: 100%;
  margin: 0.5em auto;
  text-align: center;
  letter-spacing: 1px;
}


/* For when the screen gets too narrow - mainly for mobile view */
@media screen and (max-width: 700px) {
  #container {
    display: grid;
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
    margin: auto;
    padding: 0 2em;
  }

  #name-container {
    grid-column: 1;
    grid-row: 1;
  }

  #sidebar-container {
    grid-column: 1;
    grid-row: 2;
  }

  main {
    grid-column: 1;
    grid-row: 3;
  }

  #newsfeed-navbar {
    align-content: center;
  }

}
</style>