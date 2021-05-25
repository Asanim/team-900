<template>
  <vs-card id="container">
    <div id="header-container">
      <div id="title"> Inventory </div>
      <div id="header-buttongroup">
        <vs-button class="header-button" @click="$router.push(`/businesses/${$route.params.id}/products`)">Product Catalogue</vs-button>
        <vs-button @click="addNewInv=true" class="header-button">New Inventory Listing</vs-button>
        <vs-button @click="newListingPopup = true" class="header-button">New Item Listing</vs-button>
        <!-- todo: remove this new item listing button when testing done
              - want to make it so there's an action button for each inventory item, with the ability to add a new listing attached.-->

        <vs-popup classContent="popup-example"  title="Add a product to your inventory" :active.sync="addNewInv">
        <div class="form-group required vs-col" vs-order="1" id="firstColModal">
            <div class="row">
              <label for="prodId">Product *</label>
              <vs-select id="prodId" class="selectExample" v-model="prodId" v-on:change="autofill">
                <vs-select-item :value="product.id" :text="product.name" v-for="product in products" v-bind:href="product.id" :key="product.id"/>
              </vs-select>
            </div>
            <div class="row">
              <label for="pricePerItem">Price per item *</label>
              <vs-input
                  :danger="(errors.includes(pricePerItem))"
                  danger-text="Price per item must be greater than zero and numeric."
                  class="inputx"
                  id="pricePerItem"
                  placeholder="Price per item"
                  v-model="pricePerItem"/>
            </div>
          <div class="row">
            <label for="totalPrice">Total price *</label>
            <vs-input
                :danger="(errors.includes(totalPrice))"
                danger-text="Total price must be greater than zero and numeric."
                class="inputx"
                id="totalPrice"
                placeholder="Price per item"
                v-model="totalPrice"/>
          </div>
            <div class="row">
              <label for="quantity">Quantity *</label>
              <vs-input-number
                  :danger="(errors.includes(quantity))"
                  danger-text="Quantity must be greater than zero."
                  min="0"
                  :step="1"
                  id="quantity"
                  v-model="quantity"/>
            </div>
          </div>
          <div class="form-group required vs-col" vs-order="2" id="secondColModal">
            <div class="row">
              <label for="bestBefore">Best before *</label>
              <vs-input
                  :danger="(errors.includes('past-best'))"
                  danger-text="Date cannot be in past"
                  type="date"
                  id="bestBefore"
                  class="inputx"
                  v-model="bestBefore"/>
            </div>
            <div class="row">
              <label for="listingExpiry">Listing expiry *</label>
              <vs-input
                  :danger="(errors.includes('past-expiry'))"
                  danger-text="Expiry date is required and cannot be in past"
                  type="date"
                  id="listingExpiry"
                  class="inputx"
                  v-model="listExpiry"/>
            </div>
            <div class="row">
              <label for="manufactureDate">Manufacture date *</label>
              <vs-input
                  :danger="(errors.includes('future-manu'))"
                  danger-text="Date cannot be in the future"
                  type="date"
                  id="manufactureDate"
                  class="inputx"
                  v-model="manufactureDate"/>
            </div>
            <div class="row">
              <label for="sellBy">Sell by *</label>
              <vs-input
                  :danger="(errors.includes('past-sell'))"
                  danger-text="Date cannot be in past"
                  type="date"
                  id="sellBy"
                  class="inputx"
                  v-model="sellBy"/>
            </div>
          </div>
          <div class="form-group required vs-col" align="center" id="addButton" @click="addInventory()">
            <vs-button>Add product</vs-button>
          </div>
        </vs-popup>
      </div>
    </div>

    <!-- ===== NEW LISTING MODAL ===== -->
    <vs-popup :active.sync="newListingPopup"
              title="Create a new listing">
      <div class="new-listing-modal">
        <div class="row">
          <label for="InvId">Inventory Item ID</label>
          <!-- TODO: add v-on:change to change Product name -->
          <vs-select id="InvId" class="selectExample" v-model="invItem" v-on:change="changeInvVals">
            <vs-select-item :value="invItem" :text="' (' + invItem.id +') '+ invItem.productName" v-for="invItem in inventory" v-bind:href="invItem.id" :key="invItem.id"/>
          </vs-select>
        </div>
        <div id="listing-product-name">
          <div class="sub-header">Inventory Item Name</div>
          <div style="font-size: 18px; text-align: left; font-weight: bold" v-if="invItem.length !== 0">{{ invItem.product.name }}</div>
          <div style="font-size: 10px; text-align: left" v-if="invItem.length !== 0">Price {{ invItem.pricePerItem }}</div>
          <div style="font-size: 10px; text-align: left" v-if="invItem.length !== 0">Sell By {{ invItem.sellBy }}</div>
          <div style="font-size: 10px; text-align: left" v-if="invItem.length !== 0">Qty {{ invItem.quantity }}</div>

          <div style="font-size: 18px;" v-else></div>
        </div>
        <vs-divider id="listing-divider"></vs-divider>
        <div id="listing-price">
          <span :class="{currencyAfterError: newListingErrors.price.error, currencyBeforeError: !newListingErrors.price.error}">{{currency}}</span>
          <vs-input type="number" v-model="price"
                    label="Price" min="0"
                    :danger="newListingErrors.price.error"
                    :danger-text="newListingErrors.price.message"></vs-input>
        </div>
        <div id="listing-qty">
          <div>
            <label style="font-size: 13.6px">Quantity</label>
            <vs-input-number
                v-model="listingQuantity"
                :max="10"></vs-input-number> <!-- todo: max based on current inventory amount -->
          </div>
          <div v-show="newListingErrors.quantity.error" style="font-size: 10px; color: #FF4757; text-align: center; position: absolute">{{ newListingErrors.quantity.message }}</div>
        </div>
        <div id="listing-closes">
          <vs-input type="datetime-local"
                    v-model="closes"
                    :danger="newListingErrors.closes.error"
                    :danger-text="newListingErrors.closes.message"
                    label="Close Date"></vs-input>
        </div>
        <div id="listing-moreInfo">
          <vs-textarea class="textarea"
                       v-model="moreInfo"
                       label="More Info"></vs-textarea>
        </div>
        <div id="listing-button-group">
          <vs-button id="create-button" color="success" @click="createNewListing()" style="width: 100px;">Create</vs-button>
          <vs-button id="cancel-button" color="danger" style="width: 100px;" @click="closeNewListing()">Cancel</vs-button>
        </div>
      </div>
    </vs-popup>

    <vs-divider></vs-divider>
    <!-- Table View -->
    <vs-table id="table"
              :data="this.inventory"
              noDataText="You don't have any inventory."
              :pagination="true"
              :maxItems="5"
              stripe>
      <template class="table-head" slot="thead" >
        <vs-th sort-key="id" style="border-radius: 8px 0 0 0; width: 40px;"> ID</vs-th>
        <vs-th sort-key="productId" > Image </vs-th>
        <vs-th sort-key="productName"> Product </vs-th>
        <vs-th class="dateInTable" sort-key="manufactured"> Manufactured </vs-th>
        <vs-th class="dateInTable" sort-key="sellBy"> Sell By </vs-th>
        <vs-th class="dateInTable" sort-key="bestBefore"> Best Before </vs-th>
        <vs-th class="dateInTable" sort-key="expires"> Expires </vs-th>
        <vs-th sort-key="quantity"> Qty </vs-th>
        <vs-th id="pricePerItemCol"  sort-key="pricePerItem"> Price Per Item </vs-th>
        <vs-th sort-key="totalPrice"> Total Price </vs-th>
        <vs-th style="border-radius: 0 8px 0 0"> <!-- Action Button Column --> </vs-th>
      </template>
      <template slot-scope="{data}">
        <vs-tr v-for="inventory in data" :key="inventory.id">
          <vs-td :data="inventory.id" style=" text-align: center; font-weight: bold; font-size: 10pt"> ({{inventory.id}}) </vs-td>
          <vs-td id="productIdCol" :data="inventory.productId">
          {{inventory.productId}}
          <div style="height: 80px">
            <img v-if="inventory.product.primaryImagePath != null && isDevelopment()" style="width: 100%; height: 100%;   border-radius: 1em;" v-bind:src="require('../../../backend/src/main/resources/media/images/businesses/' + getImgUrl(inventory.product))"/>
            <img v-if="inventory.product.primaryImagePath != null && !isDevelopment()" style="width: 100%; height: 100%;   border-radius: 1em;" v-bind:src="getImgUrl(inventory.product)"/>
            <img v-if="!inventory.product.primaryImagePath" style="width: 100%; height: 100%;   border-radius: 1em;" v-bind:src="require('../../public/ProductShoot.jpg')"/>
          </div>
            </vs-td>
          <vs-td :data="inventory.productName"> {{inventory.productName}} </vs-td>
          <vs-td :data="inventory.manufactured"> {{inventory.manufactured}} </vs-td>
          <vs-td :data="inventory.sellBy"> {{inventory.sellBy}} </vs-td>
          <vs-td :data="inventory.bestBefore">{{inventory.bestBefore}} </vs-td>
          <vs-td :data="inventory.expires">{{inventory.expires}} </vs-td>
          <vs-td :data="inventory.quantity">{{inventory.quantity}} </vs-td>
          <vs-td :data="inventory.pricePerItem">{{currency}}{{inventory.pricePerItem}} </vs-td>
          <vs-td :data="inventory.totalPrice">{{currency}}{{inventory.totalPrice}}</vs-td>
          <vs-td> </vs-td>
        </vs-tr>
      </template>
    </vs-table>

  </vs-card>
</template>

<script>
import axios from "axios";
import api from "../Api";
import {store} from "../store";

export default {
  name: "BusinessInventory",

  data: function() {
    return {
      errors: [],
      inventory: [],
      currency: "$",
      products: [],
      prodId:'',
      invItem:[],
      addNewInv:false,
      pricePerItem: 0.0,
      totalPrice: 0.0,
      quantity: 0,

      bestBefore: '',
      listExpiry: '',
      manufactureDate: '',
      sellBy: '',


      // New Sale Listing Variables
      newListingPopup: false,
      newListingErrors: {
        price: {error: false, message: "Price cannot be empty or negative."},
        quantity: {error: false, message: "Please enter a positive quantity."},
        closes: {error: false, message: "Please enter a valid date."}
      },
      listingQuantity: 0,
      price: "0",
      moreInfo: "",
      closes: "", // todo: should default to the expiry date of selected item.

    }
  },


  mounted() {
    this.getSession();
    this.getProducts(this.$route.params.id);
    this.getBusinessInventory();
  },

  methods: {
    isDevelopment() {
      return (process.env.NODE_ENV === 'development')
    },

    getProducts(businessId) {
      api.getBusinessProducts(businessId)
        .then((response) => {
          this.products = response.data;
          this.$log.debug("Data loaded: ", this.products);
        })
        .catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to load products";
        });

    },

    changeInvVals: function() {
      if (this.invItem !== undefined) {
        this.price = this.invItem.totalPrice;
        this.listingQuantity = this.invItem.quantity;
      }
    },
    /**
     * Validates the fields for a new public listing.
     * @return true if all of the required fields meet the requirements, false otherwise.
     */
    validateNewListing: function() {
      Object.values(this.newListingErrors).forEach(input => input.error = false);

      let isValid = true;
      if (this.price < 0 || this.price == "") {
        this.price = 0.00;
        this.newListingErrors.price.error = true;
        isValid = false;
      }
      if (this.listingQuantity < 1) { // In theory this shouldn't occur (because vs-input-number component will set it to the min/max allowed).
        this.listingQuantity = 0;
        this.newListingErrors.quantity.error = true;
        isValid = false;
      }
      if (this.closes === "" || this.closes == null) {
        this.newListingErrors.closes.error = true;
        this.newListingErrors.closes.message = "Please enter a valid date.";
        isValid = false;
      }
      if (new Date(this.closes) < Date.now()) {
        this.newListingErrors.closes.error = true;
        this.newListingErrors.closes.message = "Please enter a future date.";
        isValid = false;
      }

      return isValid;
    },

    getImgUrl(product) {
      console.log("PRODUCTT: " + product);
      if (product.primaryImagePath != null && process.env.NODE_ENV !== 'development' && process.env.NODE_ENV !== 'staging') {
        return '/prod/prod_images/' + product.primaryImagePath.toString().replace("\\", "/")
      } else if (product.primaryImagePath != null && process.env.NODE_ENV !== 'development') {
        return '/test/prod_images/' + product.primaryImagePath.toString().replace("\\", "/")
      } else if (product.primaryImagePath != null) {
        return product.primaryImagePath.toString().replace("\\", "/")
      } else {
        return '../../public/ProductShoot.jpg'
      }
    },

    /**
     * Checks if the new list form is valid, then creates a new listing to be sent to backend if true.
     */
    createNewListing: function() {
      if (this.validateNewListing()) {
        if (this.errors.length === 0) {
          api.createListing(store.actingAsBusinessId, this.invItem.id, this.listingQuantity, this.price, this.moreInfo, this.closes)
            .then((response) => {
              this.$log.debug("New listing has been posted:", response.data);
            })
            .catch((error) => {
              if (error.response) {
                console.log(error);
                if (error.response.status === 400) {
                  this.$vs.notify( {
                    title: 'Failed to add a listing',
                    text: 'Incomplete form, or the product does not exist.',
                    color: 'danger'
                  });
                }
                else if (error.response.status === 403) {
                  this.$vs.notify( {
                    title: 'Failed to add a listing',
                    text: 'You do not have the rights to access this business',
                    color: 'danger'
                  });
                }
                console.log(error.response.status);
              }
              this.$log.debug("Error Status:", error)
            });
        }
      }
    },

    /**
     * Closes the new listing modal, and reset error fields.
     */
    closeNewListing: function() {
      this.newListingPopup = false;
      Object.values(this.newListingErrors).forEach(input => input.error = false);
    },

    /**
     * Calls get session endpoint to get user country, if successful calls setCurrency ()
     */
    getSession: function() {
      api.checkSession()
      .then((response) => {
        //Call set currency inside here to currency is not removed when page refreshes
        this.setCurrency(response.data.homeAddress.country)
      }).catch(err => {
        this.$log.debug(err);
      })
    },

    /**
     * Sets display currency based on the user's home country.
     */
    setCurrency: function (country) {
      axios.get(`https://restcountries.eu/rest/v2/name/${country}`)
        .then(response => {
          this.currency = response.data[0].currencies[0].symbol;
        }).catch(err => {
          this.$log.debug(err);
      });
    },
    checkForm: function() {
      this.errors = [];
      var invalidChars = /[^a-zA-Z/ -\d]/i;
      if (this.prodId.match(invalidChars)) {
        this.errors.push("invalid-chars");
      }

      var today = new Date();
      var regex = /^[0-9]*(?:\.\d{1,2})?$/;
      if( !regex.test(this.pricePerItem) ) {
        this.errors.push(this.pricePerItem);
      }

      var dateInPast = function(firstDate, secondDate) {
        if(firstDate.setHours(0,0,0,0) <= secondDate.setHours(0,0,0,0)) {
          return true;
        }

        return false;
      }

      var dateInFuture = function(firstDate, secondDate) {
        if(firstDate.setHours(0,0,0,0) >= secondDate.setHours(0,0,0,0)) {
          return true;
        }

        return false;
      }

      if (this.bestBefore === '' && this.sellBy === '' && this.manufactureDate === ''
          && this.listExpiry === '') {
        this.errors.push('no-dates');
      }

      if (this.listExpiry === '') {
        this.errors.push('past-expiry');
      }

      if (this.prodId.length === 0 || this.errors.includes('invalid-chars')) {
        this.errors.push(this.prodId);
      }
      if (this.pricePerItem <= 0.0) {
        this.errors.push(this.pricePerItem);
      }
      if (this.bestBefore !== '') {
        var timestamp = Date.parse(this.bestBefore);
        var dateObject = new Date(timestamp)
        if (dateInPast(dateObject, today) === true) {
          this.errors.push('past-date');
          this.errors.push('past-best');
        }
      }
      if (this.listExpiry !== '') {
        timestamp = Date.parse(this.listExpiry);
        dateObject = new Date(timestamp)
        if (dateInPast(dateObject, today) === true) {
          this.errors.push('past-date');
          this.errors.push('past-expiry');
        }
      }
      if (this.manufactureDate !== '') {
        timestamp = Date.parse(this.manufactureDate);
        dateObject = new Date(timestamp)
        if (dateInFuture(dateObject, today) === true) {
          this.errors.push('past-date');
          this.errors.push('future-manu');
        }
      }
      if (this.sellBy !== '') {
        timestamp = Date.parse(this.sellBy);
        dateObject = new Date(timestamp)
        if (dateInPast(dateObject, today) === true) {
          this.errors.push('past-date');
          this.errors.push('past-sell');
        }
      }
      if (this.quantity <= 0) {
        this.errors.push(this.quantity);
      }
      if (this.errors.includes('no-dates')) {
        this.$vs.notify({
          title: 'Failed to create inventory item',
          text: 'Date is Required.',
          color: 'danger'
        });
      }
      if (this.errors.includes('past-date')) {
        this.$vs.notify({
          title: 'Failed to create inventory item',
          text: 'Dates cannot be in the past.',
          color: 'danger'
        });
      }
      if (this.errors.includes('no-desc')) {
        this.$vs.notify({
          title: 'Failed to create inventory item',
          text: 'Description (MAX 250 CHARS).',
          color: 'danger'
        });
      }
      if (this.errors.includes(this.quantity)) {
        this.$vs.notify({
          title: 'Failed to create inventory item',
          text: 'Quantity must be greater than zero.',
          color: 'danger'
        });
      }
      if (this.errors.length > 0) {
        return false;
      }
      return true;
    },
    addInventory: function() {
      if (this.checkForm()) {

        // //automatically calculate the total price
        // if (this.totalPrice >= 0) {
        //   this.totalPrice = this.quantity*this.pricePerItem;
        // }

        console.log("DEBUG: " + this.totalPrice);

        api.createInventory(store.actingAsBusinessId, this.prodId, this.quantity, this.pricePerItem, this.totalPrice, this.manufactureDate, this.sellBy, this.bestBefore, this.listExpiry)
          .then((response) => {
            this.$log.debug("New catalogue item created:", response.data);
            this.inventory.push(response.data);
            this.addNewInv = false;
            this.getBusinessInventory();
            this.$vs.notify( {
              title: `Item successfully added to the business' inventory`,
              color: 'success'
            });
          }).catch((error) => {
            if (error.response) {
              console.log(error);
              if (error.response.status === 400) {
                if (this.getActingAsBusinessId() == null) {
                  this.$vs.notify( {
                    title: 'Failed to add an inventory item',
                    text: 'User is not a business administrator',
                    color: 'danger'
                  });
                } else {
                  this.$vs.notify( {
                    title: 'Failed to add an inventory item',
                    text: 'Incomplete form, or the product does not exist.',
                    color: 'danger'
                  });
                }


              } else if (error.response.status === 403) {
                this.$vs.notify( {
                  title: 'Failed to add an inventory item',
                  text: 'You do not have the rights to access this business',
                  color: 'danger'
                });
              } else if (error.response.status === 404) {
                this.$vs.notify( {
                  title: 'Failed to add an inventory item',
                  text: 'There was a problem adding the inventory item to the database',
                  color: 'danger'
                });
              }
              console.log(error.response.status);
            }
          this.$log.debug("Error Status:", error)
        })
      }
    },
    autofill: function() {
      if (this.prodId !== '') {
        let prodInd = 0;
        while (prodInd < this.products.length) {
          if (this.products[prodInd]["id"] === this.prodId) {
            break;
          }
          prodInd++;
        }
        console.log(this.products[prodInd])
        this.pricePerItem = this.products[prodInd]["recommendedRetailPrice"];
      }
    },

    getActingAsBusinessId() {
      return store.actingAsBusinessId;
    },


    /**
     * Calls API get businessInventory function, also adds new fields to inventory object for sorting and sets default order
    */
    getBusinessInventory() {
      api.getBusinessInventory(this.$route.params.id)
      .then((response) => {
        this.inventory = response.data;
        console.log(this.inventory)
        for(let inventoryItem of this.inventory) {
          //Issue with sorting using object properties, so pulled required properties into inventory object
          inventoryItem['productName'] = inventoryItem.product.name;
          inventoryItem['productId'] = inventoryItem.product.id;
        }

        //Default ordering is product name, so all similar products will be next to each other
        this.inventory = this.inventory.sort((productOne, productTwo) => (productOne.name < productTwo.name) ? 1 : -1)

      }).catch((err) => {
        if(err.response.status == 401) {
          this.$router.push({path: '/login'})
        }
      })
    }
  }
}


</script>

<style scoped>
  #container {
    width: 75%;
    margin: 1em auto;
  }

  /* ===== PAGE HEADER ===== */
  #header-container {
    display: flex;
    justify-content: space-between;
    padding-top: 4px;
  }

  #title {
    font-size: 30px;
    margin: auto 8px;
  }

  #header-buttongroup {
    display: inline-flex;
    justify-content: space-around;
  }

  .header-button {
    margin: 0 1em;
  }

  /* ===== NEW LISTING DIALOG/PROMPT ====== */

  .new-listing-modal {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: repeat(auto-fit, 1fr);
    grid-row-gap: 0.5em;
  }

  .vs-popup-primary >>> header {
    background-color: #1F74FF;
    color: #FFFFFF;
  }

  .textarea >>> textarea {
    resize: none;
    max-width: 200px;
    min-height: 100px;
    max-height: 100px;
  }

  #listing-product-id {
    grid-row: 1;
    grid-column: 1;

    text-align: center;
  }

  #listing-product-name {
    grid-row: 1;
    grid-column: 2;

    text-align: center;
  }

  #listing-divider {
    grid-row: 2;
    grid-column: 1 / 3;
  }

  #listing-qty {
    margin: auto;
    grid-column: 2;
    grid-row: 3;
  }

  #listing-price {
    margin: auto;
    grid-column: 1;
    grid-row: 3;

    display: flex;
  }

  #listing-closes {
    margin: auto;
    padding-left: 12px;
    grid-column: 1;
    grid-row: 4;
  }

  #listing-moreInfo {
    margin: 0.75em auto auto auto;
    width: 90%;
    grid-row: 5;
    grid-column: 1 / 3;
  }

  #listing-button-group {
    margin: 0.75em auto auto auto;
    width: 90%;
    grid-row: 6;
    grid-column: 2;

    display: flex;
    justify-content: space-around;
  }

  .sub-header {
    font-size: 12px;
    color: gray;
  }

  .currencyBeforeError {
    margin: auto 4px 4px 0;
  }

  .currencyAfterError {
    margin: auto 4px 32px 0;
  }


  /* ===== INVENTORY TABLE ===== */
  .table-head {
    border-radius: 1em;
  }

  th {
    background: #1F74FF;
    color: white;
    font-size: 12px;
  }

  #table {
    margin: 0.5em;
    white-space: nowrap;
  }

  #productIdCol {
    font-size: 10px;
  }

  #pricePerItemCol {
    font-size: 11px;
  }

  td {
    font-size: 12px;
    min-width: 80px
  }

  .dateInTable{
    width: 130px;
  }




  /* ===== INVENTORY ADDING MODAL ===== */
  #firstColModal {
    margin-right: 160px;
    margin-left: 5px;
  }

  .description-textarea >>> textarea {
    resize: none;
    min-height: 50px;
    max-height: 50px;
  }

  .row {
    margin-bottom: 15px;
  }
  .addButton {
    align-self: center;
  }

  @media screen and (max-width: 850px) {
    #header-container {
      flex-direction: column;
    }

    #title {
      padding: 0.5em 0 1em 0.5em;
    }
  }

  @media screen and (max-width: 450px) {
    .new-listing-modal {
      display: grid;
      grid-template-columns: 1fr;
      grid-template-rows: repeat(auto-fit, 1fr);
      grid-row-gap: 0.5em;
    }

    #listing-product-id {
      grid-row: 1;
      grid-column: 1;
    }

    #listing-product-name {
      grid-row: 2;
      grid-column: 1;

    }

    #listing-divider {
      grid-row: 3;
      grid-column: 1;
    }

    #listing-qty {
      grid-column: 1;
      grid-row: 4;
      margin-bottom: 1em;
    }

    #listing-price {
      grid-column: 1;
      grid-row: 5;
    }

    #listing-closes {
      grid-column: 1;
      grid-row: 6;
    }

    #listing-moreInfo {
      grid-row: 7;
      grid-column: 1;
    }

    #listing-button-group {
      grid-row: 8;
      grid-column: 1;

      display: flex;
      justify-content: space-around;
    }

    .textarea >>> textarea {
      resize: none;
      min-height: 100px;
      max-height: 100px;
      max-width: 100px;
    }

  }

</style>