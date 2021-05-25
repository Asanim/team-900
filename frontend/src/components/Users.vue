<template>
  <div>
    <div id="container" v-if="this.user != null">

      <!-- Far left side options menu-->
      <div id="options-bar" v-if="showOptionsMenu()">
        <div class="sub-header" style="text-align: center"> Options </div>
        <div class="options-card" id="option-add-to-business" v-if="this.userViewingBusinesses.length >= 1" @click="openModal()"> Add to Business </div>
      </div>

      <div id="name-container">
        <div id="full-name"> {{ this.user.firstName }} {{ this.user.middleName }} {{ this.user.lastName }} </div>
        <div id="nickname"> {{ this.user.nickname }} </div>
      </div>

      <!-- Left Profile Side -->
      <div id="profile">
        <div id="bio" class="sub-container">
          <div class="sub-header">Bio</div>
          {{ this.user.bio }}
        </div>

        <div class="sub-container" id="info-container">
          <div id="created">
            <div class="sub-header">Member Since</div>
            {{ this.user.created.split(' ')[0] }}
            <div style="font-size: 12px">{{ calculateDuration(user.registerDate) }}</div>

          </div>
          <div id="email">
            <div class="sub-header">Email</div>
            {{ this.user.email }}
          </div>
          <div id="birthDate">
            <div class="sub-header">Date of Birth</div>
            {{ this.user.dateOfBirth }}
          </div>
          <div id="address">
            <div class="sub-header">Home Address</div>
              <div id="street-address">{{ user.homeAddress.streetNumber }} {{ user.homeAddress.streetName }}</div>
              <div id="suburb">{{ user.homeAddress.suburb }}</div>
              <div id="city">{{ user.homeAddress.city }}</div>
              <div id="region">{{ user.homeAddress.region }}</div>
              <div id="country">{{ user.homeAddress.country }}</div>
              <div id="postcode">{{ user.homeAddress.postcode }}</div>
          </div>
          <div id="phonenumber">
            <div class="sub-header">Phone Number</div>
            {{ this.user.phoneNumber }}
          </div>
        </div>

      </div>

      <!-- Right Content Side -->
      <main>
        <ul id="business-list">
          <li class="card" v-for="business in businesses" :key="business.id" v-bind:business="business" @click="goToBusinessPage(business)">
            <div class="card-name">{{ business.name }}</div>
            <div class="card-type">{{ business.businessType }}</div>
            <div class="card-description">{{ business.description }}</div>
          </li>
        </ul>
      </main>
  </div>

    <!-- Add user to business as admin modal -->
    <Modal v-if="showModal">
      <div slot="header">
        Add user to business:
      </div>

      <div slot="body">
          <vs-select class="business-dropdown" v-model="selectedBusiness">
            <vs-select-item v-for="business in userViewingBusinesses" :key="business.id" :text="business.name" :value="business"/>
          </vs-select>
      </div>

      <div id="modal-footer" slot="footer">
        <button class="modal-button modal-cancel-button" @click="closeModal()">
          Cancel
        </button>
        <button class="modal-button modal-ok-button" id="add-user" @click="addUserToBusiness()">
          Add
        </button>
      </div>
    </Modal>

  </div>
</template>


<script>
import Modal from "./Modal";
import api from "../Api";
const moment = require('moment');
import {store} from "../store";


const Users = {
  name: "Profile",
  components: {Modal},
  data: function () {
    return {
      user: null,
      businesses: [],
      userViewingBusinesses: [],

      showModal: false,
      selectedBusiness: null
    };
  },

  methods: {
    /**
     * Show the modal box.
     * Having a separate function to just open the modal is good for testing.
     */
    openModal: function() {
      this.showModal = true;
    },

    /**
     * Close the pop-up box with no consequences.
     */
    closeModal: function() {
      this.showModal = false;
    },

    /**
     * Called when the pop-up box has the OK button pressed. Add the user to the given business as an admin.
     */
    addUserToBusiness: function() {
      api.makeUserBusinessAdmin(this.selectedBusiness.id, this.user.id)
        .then(() => {
          // 200 code.
          this.businesses.push(this.selectedBusiness);
          this.$vs.notify({title:`Added user to ${this.selectedBusiness.name}`, text:`Successfully added ${this.user.firstName} as an administrator.`, color:'success'});
          this.closeModal();
        })
        .catch((error) => {
          if (error.response.status === 400) {
            this.$vs.notify({title:`Failed to add user to ${this.selectedBusiness.name}`, text:`${this.user.firstName} is already an administrator.`, color:'danger'});
          }
          else {
            throw new Error(`Error trying to add user to business: ${error.response.status}`);
          }
        });
    },

    calculateDuration: function(registerDate) {
      const TimeElapsed = Date.now();
      const today = new Date(TimeElapsed);
      let fromTime = moment(registerDate).diff(today);
      let duration = moment.duration(fromTime);

      let timeString = "(";
      if ((duration._data.years / -1) > 1) timeString += duration._data.years / -1 + " years ";
      if ((duration._data.months / -1 ) > 1) timeString += duration._data.months / -1 + " months";
      else timeString += "under 1 month";
      timeString += ")";

      return timeString;
    },

    /**
     * Performs a get request to get user info to display on page
     * @param userId ID of user that is currently being viewed
     */
    getUserInfo: function(userId) {
      api.getUserFromID(userId) //Get user data
        .then((response) => {
          if(store.userBusinesses != null){
            this.userViewingBusinesses = store.userBusinesses;
          }
          this.user = response.data;
          this.businesses = JSON.parse(JSON.stringify(this.user.businessesAdministered));
        }).catch((err) => {
          this.$vs.notify({title:'Unauthorized Action', text:'You must login first.', color:'danger'});
          this.$router.push({path: "/login"}); //If user not logged in send to login page
          throw new Error(`Error trying to get user info from id: ${err}`);
      });
    },

    /**
     * Redirects the browser to the business page that was pressed on.
     * @param business id to redirect to.
     */
    goToBusinessPage: function(business) {
      this.$router.push({path: `/businesses/${business.id}`})
    },

    showOptionsMenu: function() {
      if (this.userViewingBusinesses < 1) {
        return false
      }
      return true
    },

  },

  mounted: function () {
  //On page load call getUserInfo function to get user information
    let userId = this.$route.params.id
    this.getUserInfo(userId);
  },
}

export default Users;
</script>

<style scoped>

#container {
  display: grid;
  grid-template-columns: 1fr 1fr 4fr 1fr;
  grid-template-rows: 1fr auto;
  grid-column-gap: 1em;
  margin: auto;
  padding: 0 2em;
}

/* Options Bar */
#options-bar {
  grid-column: 1;
  grid-row: 2;

  padding: 2em;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #F5F5F5;
  margin: 1em 0 1em 0;

}


.options-card {
  cursor: pointer;

  text-align: center;
  color: black;
  font-weight: 500;
  font-size: 14px;
  letter-spacing: 1px;
  text-decoration: none;

  padding: 10px 20px;
  margin: 1em;
  background: #dbe0dd linear-gradient(to right, #abd9c1 10%, #fceeb5 50%, #ee786e 100%);
  background-size: 500%;
  border: none;
  border-radius: 4px;
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15);
}

.options-card:hover {
  box-shadow: 0 0.25em 1em rgba(0,1,1,.25);
}

/* Name Header */
#name-container {
  grid-column: 2 / 4;

  text-align: center;

  background-color: transparent;
  padding: 0.5em 0 0.5em 0;
  border-radius: 4px;
  border: 2px solid rgba(0, 0, 0, 0.02);
  margin: 8px 0 0 0;
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15);

}

#full-name {
  font-size: 32px;
  padding: 0.5em 0 0.5em 0;
}

#nickname {
  font-size: 16px;
  padding: 0 0 0.5em 0;
}

/* Left Profile Side */
#profile {
  grid-column: 2;
  grid-row: 2;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(3, auto) repeat(1, 1fr);
  grid-row-gap: 1em;
}

.sub-header {
  font-size: 12px;
  color: gray;
}

.sub-container {
  padding: 2em;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #F5F5F5;
}

#bio {
  grid-column: 1;
  grid-row: 2;
}

#info-container {
  grid-column: 1;
  grid-row: 3;

  font-size: 16px;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(3, auto) repeat(1, 1fr);
  grid-row-gap: 1em;

}

/* Main Content Side */
main {
  grid-column: 3;
  grid-row: 2;

  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  margin: 1em 0 1em 0;
  padding: 0;
  background-color: #F5F5F5;
}

/* Business Card Component Related */
#business-list {
  padding: 1em;
}

.card {
  background-color: transparent;
  padding: 1em;
  border-radius: 4px;
  border: 2px solid rgba(0, 0, 0, 0.02);
  margin: 1em;
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15);

  display: grid;
  grid-template-columns: auto auto;
  grid-template-rows: auto auto auto;
}

.card:hover {
  box-shadow: 0 0.5em 1em rgba(0,1,1,.25);
  cursor: pointer;
}

.card >>> .card-name {
  grid-row: 1;
  grid-column: 1;

  padding: 0.25em 0 0.25em 0;
  font-size: 24px;
}

.card >>> .card-type {
  grid-row: 1;
  grid-column: 2;

  text-align: end;
  height: fit-content;
  line-height: normal;
  font-size: 16px;
  padding: 0.5em 0 1em 0;
}

.card >>> .card-description {
  grid-column: 1/3;
  font-size: 12px;
  padding: 0.5em 0 0.5em 0;
}

/* For when the screen gets too narrow - mainly for mobile view */
@media screen and (max-width: 700px) {
  #container {
    display: grid;
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto auto;
  }

  #name-container {
    grid-column: 1;
    grid-row: 1;
  }

  #options-bar {
    grid-column: 1;
    grid-row: 2;
  }

  #profile {
    grid-column: 1;
    grid-row: 3;
  }

  main {
    grid-column: 1;
    grid-row: 4;
    margin: 0;
  }

  .card {
    max-width: 100%;
  }

}


/* Make User Admin Popup Box */
.business-dropdown {
  text-align: center;
  font-size: 14px;
  text-decoration: none;

  width: 100%;
  padding: 12px 16px;
}

#modal-footer {
  margin: auto;
}

.modal-button:hover {
  box-shadow: 0 0.25em 1em rgba(0,1,1,.25);
}

.modal-ok-button {
  text-align: center;
  color: black;

  width: 100px;
  margin: 0 1em;
  padding: 10px 20px;
  background: #dbe0dd linear-gradient(to right, #abd9c1 10%, #fceeb5 50%, #ee786e 100%);
  background-size: 500%;
  border: none;
  border-radius: 4px;
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15);

}

.modal-cancel-button {
  text-align: center;
  color: black;

  width: 100px;
  margin: 0 1em;
  padding: 10px 20px;
  background: #dbe0dd linear-gradient(to left, #abd9c1 10%, #fceeb5 50%, #ee786e 100%);
  background-size: 500%;
  border: none;
  border-radius: 4px;
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15);
}



</style>