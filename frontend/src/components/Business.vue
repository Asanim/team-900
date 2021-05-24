<template>
  <div>
    <div id="container" v-if="this.business != null">
      <!-- Left Side Business Information Panel -->
      <div id="business-name-container">
        <div id="business-name"  >{{ business.name }}</div>
        <div id="business-type">{{ business.businessType }}</div>
      </div>

      <div id="business-container">
        <div id="description" class="sub-container">
          <div class="sub-header">Description</div>
          {{ business.description }}
        </div>

        <div id="info-container" class="sub-container">
          <div id="created-date">
            <div class="sub-header">Created</div>
            {{ business.created.split(' ')[0] }}
          </div>
          <div id="address">
            <div class="sub-header">Address</div>
            <div id="street-address">{{ business.address.streetNumber }} {{ business.address.streetName }}</div>
            <div id="city">{{ business.address.city }}</div>
            <div id="suburb">{{ business.address.suburb }}</div>
            <div id="region">{{ business.address.region }}</div>
            <div id="country">{{ business.address.country }}</div>
            <div id="postcode">{{ business.address.postcode }}</div>
          </div>
        </div>
      </div>

      <main>
        <!-- Sub Navigation Bar -->
        <vs-tabs id="business-navbar"> <!-- id="business-navbar" -->
          <vs-tab class="business-nav-item" label="Listings">
            <BusinessListings :business-id="business.id" :country="user.homeAddress.country"/>
          </vs-tab>
          <vs-tab class="business-nav-item" label="Administrators">
            <BusinessAdministrators :admins="adminList" :pAdminId="business.primaryAdministratorId"/>
          </vs-tab>
        </vs-tabs>
      </main>

    </div>
  </div>
</template>


<script>
import api from "../Api";
import BusinessAdministrators from "./BusinessAdministrators";
import BusinessListings from "./BusinessListings";
//import {store} from "../store";

const Business = {
  name: "Business",
  components: {BusinessListings, BusinessAdministrators},

  // App's initial state.
  data: function () {
    return {
      business: null,
      adminList: null,
      user: null
    };
  },

  methods: {
    getBusiness: function () {
      api.getBusinessFromId(this.$route.params.id)
          .then((res) => {
            this.business = res.data;
            this.adminList = JSON.parse(JSON.stringify(this.business.administrators)); // It just works?
          })
          .catch((error) => {
            if (error) {
              if (error.response.status === 406) {
                this.$vs.notify({title:'Error', text:'This business does not exist.', color:'danger', position:'top-center'})
              }
            }
            this.$log.error(`ERROR trying to obtain business info from Id: ${error}`);
          });
    },

    getUserInfo: function (userId) {
      api.getUserFromID(userId)
        .then((response) => {
          this.user = response.data;
        })
        .catch((err) => {
          if (err) {
            if (err.response.status === 401) {
              this.$vs.notify({title:'Unauthorized Action', text:'You must login first.', color:'danger'});
              this.$router.push({name: 'LoginPage'});
            }
          }
          this.$log.error(`ERROR trying to obtain user info from Id: ${err}`);
      });
    },

    checkUserSession: function() {
      api.checkSession()
        .then((response) => {
          this.getUserInfo(response.data.id);
          this.getBusiness();
        })
        .catch((error) => {
          this.$vs.notify({title:'Error', text:'ERROR trying to obtain user info from session:', color:'danger'});
          this.$log.error("Error checking sessions: " + error);
        });
    }

  },

  mounted() {
    this.checkUserSession();
  }
}

export default Business;
</script>


<style scoped>

#container {
  display: grid;
  grid-template-columns: 1fr 1fr 4fr 1fr;
  grid-template-rows: auto auto;
  grid-column-gap: 1em;
}

/* === TOP BANNER BUSINESS NAME === */
#business-name-container {
  grid-column: 2 / 4;
  grid-row: 1;

  text-align: center;
  background-color: transparent;
  padding: 0.5em 0 0.5em 0;
  border-radius: 4px;
  border: 2px solid rgba(0, 0, 0, 0.02);
  margin: 8px 0 0 0;
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15);
}

#business-name {
  font-size: 32px;
  padding: 0.5em 0 0.5em 0;
}

#business-type {
  font-size: 16px;
  padding: 0 0 0.5em 0;
}

/* === LEFT SIDE INFO PANEL === */
#business-container {
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

.sub-header {
  font-size: 12px;
  color: gray;
}

#description {
  grid-row: 2;
}

#info-container {
  grid-column: 1;
  grid-row: 3;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(1, auto) repeat(1, 1fr);
  grid-row-gap: 2em;
}

#created-date {
  grid-column: 1;
  grid-row: 1;
}

#address {
  grid-row: 2;
  height: fit-content;
}

/* === MAIN CONTENT SECTION === */
main {
  grid-column: 3;
  grid-row: 2;

  margin: 1em 0 1em 0;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #F5F5F5;
}

#business-navbar {
  grid-column: 2;
  grid-row: 1;
}

.business-nav-item {
  padding: 0 1em;
  font-size: 14px;
}

#business-navbar >>> .vs-tabs--li { /* Targets individual tab */
  padding: 0 1em;
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

  #business-name-container {
    grid-column: 1;
    grid-row: 1;
  }

  #business-container {
    grid-column: 1;
    grid-row: 2;
  }

  main {
    grid-column: 1;
    grid-row: 3;
  }

  #business-navbar {
    align-content: center;
  }

}

</style>
