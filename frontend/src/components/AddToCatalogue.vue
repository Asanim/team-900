<template>
  <div class="card" v-if="this.user != null">
    <h3 class="card-header">Add a Product to your Catalogue</h3>
    <form>
      <div id="info-field">
        <div id="product-name">
          <vs-input
              :danger="(errors.includes(productName))"
              danger-text="Product name is required"
              class="form-control"
              type="text"
              label-placeholder="Product name (required)"
              v-model="productName"/>
        </div>
        <div id="product-id">
          <vs-input
              :danger="(errors.includes(productId))"
              danger-text="Product id is required"
              class="form-control"
              type="text"
              label-placeholder="Product ID (required)"
              v-model="productId"/>
        </div>
        <div id="rrp">
          <div id="currencySymbol">{{this.currencySymbol}}</div>
          <vs-input
              :danger="(errors.includes('no-rrp') || errors.includes('rrp') || errors.includes('invalid-rrp'))"
              danger-text="RRP is required and must be at least 0 and a Number."
              id="currencyInput"
              label-placeholder="Recommended Retail Price"
              type="text"
              v-model="rrp"/>
          <div id="currencyCode">{{this.currencyCode}}</div>
        </div>
        <div id="manufacturer">
          <vs-input
              :danger="(errors.includes('no-manu'))"
              danger-text="Manufacturer is Required."
              class="form-control"
              type="text"
              label-placeholder="Manufacturer"
              v-model="manufacturer"/>
        </div>
        <div id="description">
          <vs-textarea
              :danger="(errors.includes('no-desc'))"
              danger-text="Description is Required."
              class="form-control"
              type="text"
              width="400px"
              label="Description (required)"
              v-model="description"/>
        </div>
      </div>
      <button
          type="button"
          class="add-button"
          @click="checkForm(); createItem();">Add Item to Catalogue</button>
    </form>
  </div>
</template>

<script>
import CurrencyInput from "../components/CurrencyInput";
import api from "../Api";
import axios from "axios";
import {store} from "../store";

const AddToCatalogue = {
  name: "AddToCatalogue",
  components: {CurrencyInput},
  data: function () {
    return {
      user: null,
      errors: [],
      productId: "",
      productName: "",
      description: "",
      manufacturer: "",
      currencySymbol: "",
      currencyCode: "",
      rrp: ""
    };
  },
  methods: {
    /**
     * The function checks the inputs of the registration form to ensure they are in the right format.
     * The function also updates the errors list that will be displayed on the page if at least one of the input boxes
     * is in the wrong format.
     */
    checkForm: function () {
      this.errors = [];
      if (this.productName.length === 0) {
        this.errors.push(this.productName);
      }

      if (this.productId.length === 0) {
        this.errors.push(this.productId);
      }

      if (this.description.length === 0) {
        this.errors.push('no-desc');
      }

      if (this.manufacturer.length === 0) {
        this.errors.push('no-manu');
      }

      if (this.rrp.length === 0 || this.rrp === null) {
        this.errors.push('no-rrp');
      } else if (this.rrp < 0) {
        this.errors.push('rrp');
      }

      if (isNaN(this.rrp)) {
        this.errors.push('invalid-rrp');
      }

      if (this.errors.length >= 1) {
        if (this.errors.includes(this.productName) || this.errors.includes(this.productId)
            || this.errors.includes('rrp') || this.errors.includes('no-rrp')
            || this.errors.includes('invalid-rrp') || this.errors.includes('no-manu')) {
          this.$vs.notify({
            title: 'Failed to create catalogue item',
            text: 'Required fields are missing.',
            color: 'danger'
          });
        }
      }
      if (this.errors.includes('no-desc')) {
        this.$vs.notify({
          title: 'Failed to create catalogue item',
          text: 'Description is Required.',
          color: 'danger'
        });
      }
    },
    /**
     * Creates a POST request when user submits form, using the createUser function from Api.js
     */
    createItem: function () {
      //Use creatItem function of API to POST user data to backend
      //https://www.npmjs.com/package/json-server
      if (this.errors.length === 0) {
        api.createProduct(store.actingAsBusinessId, this.productId, this.productName, this.description, this.manufacturer, this.rrp)
            .then((response) => {
              this.$log.debug("New catalogue item created:", response.data);
              //this.$router.push({path: `/businesses/${store.actingAsBusinessId}/products`});
              //this.newListingPopup = false;
              this.$vs.notify({
                title: 'New catalogue item created',
                color: 'success'
              });
            }).catch((error) => {
          if (error.response) {
            console.log(error);
            if (error.response.status === 400) {
              this.$vs.notify({
                title: 'Failed to create catalogue item',
                text: 'Product ID is already in use',
                color: 'danger'
              });
            }
            console.log(error.response.status);
          }
          this.$log.debug("Error Status:", error)
        }).finally(() => {
          this.$vs.loading.close();
          location.reload();
        });
      }
    },

    getUserInfo: function (userId) {
      api.getUserFromID(userId) //Get user data
          .then((response) => {
            this.user = response.data;
            this.setCurrency(this.user.homeAddress.country);
          }).catch((err) => {
        if (err.response.status === 401) {
          this.$vs.notify({title: 'Unauthorized Action', text: 'You must login first.', color: 'danger'});
          this.$router.push({name: 'LoginPage'});
        } else {
          this.$log.debug(err);
        }
      });
    },

    setCurrency: function (country) {
      axios.get(`https://restcountries.eu/rest/v2/name/${country}`)
          .then(response => {
            this.currencySymbol = response.data[0].currencies[0].symbol;
            this.currencyCode = response.data[0].currencies[0].code;
          }).catch(err => {
        this.$log.debug(err);
      });
    },
    checkUserSession: function() {
      api.checkSession()
          .then((response) => {
            this.getUserInfo(response.data.id);
          })
          .catch((error) => {
            this.$log.debug("Error checking sessions: " + error);
            this.$vs.notify({title:'Error', text:'ERROR trying to obtain user info from session:', color:'danger'});
          });
    }
  },
  mounted: function () {
    this.checkUserSession();
  }
}

export default AddToCatalogue;

</script>

<style scoped>

/*
Add button's styling
 */
.add-button {
  grid-column: 1 / 3;

  cursor: pointer;
  border-radius: 5em;
  color: #fff;
  background: #1F74FF;
  border: 0;
  z-index: 1000;
  padding: 10px 40px;
  margin: 2em auto;
  font-size: 13px;
  box-shadow: 0 0 20px 1px rgba(0, 0, 0, 0.04);
  text-align: center;
}

/**
Card styling.
*/
.card {
  font-family: 'Ubuntu', sans-serif;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: auto auto auto;
  grid-row-gap: 1em;

  max-width: 650px;
  background-color: white;
  margin: 1em auto;
  padding: 0.5em 0 0.5em 0;
  border-radius: 20px;
  border: 2px solid rgba(0, 0, 0, 0.02);
  box-shadow: 0 .5rem 1rem rgba(0, 0, 0, .15);
}

/**
Card header styling.
*/
.card-header {
  grid-row: 1;
  grid-column: 1;

  text-align: center;
  font-weight: bold;
  font-size: 24px;
  color: #1F74FF;

  margin: 0;
  padding: 0.5em 0;
}

/**
Form styling
*/
form {
  grid-row: 2;
  grid-column: 1;

  margin: auto;

  display: grid;
  grid-template-columns: repeat(2, auto);
  grid-template-rows: repeat(2, auto);
}

label, input {
  display: block;
}

/**
Styling for form elements.
*/
.form-control {
  font-family: 'Ubuntu', sans-serif;
  padding: 3px 10px;
  margin: 0.5em;
}

#info-field {
  grid-column: 1/3;
  display: grid;
  margin: auto;
  grid-template-columns: repeat(2, auto);
  grid-template-rows: repeat(5, auto);
}

#product-name {
  grid-column: 1;
  grid-row: 1;
}

#product-id {
  grid-column: 2;
  grid-row: 1;
}

#manufacturer {
  grid-column: 2;
  grid-row: 2;
}

#description {
  grid-column: 1 / 3;
  grid-row: 3;
}

#rrp {
  grid-column: 1;
  grid-row: 2;

  margin: 0;
  display: flex;
}

#currencySymbol {
  grid-row: 1;
  grid-column: 1;
  margin: auto;
  font-size: 15px;
  line-height: 20px;
}

#currencyInput {
  grid-row: 1;
  grid-column: 2;
}

#currencyCode {
  grid-row: 1;
  grid-column: 3;

  margin: auto;
  font-size: 15px;
}


</style>