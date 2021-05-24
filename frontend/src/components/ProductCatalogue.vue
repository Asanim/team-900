<template>
  <vs-card class="main">
      <div class="profile-text-inner">
        <div style="display: flex; margin: auto; padding-bottom: 1em;">
          <h1 id="pagetitle" class="title text-center" style="font-size: 40px;">{{this.business}} Products</h1>

          <div style="margin-right: 0; margin-left: auto; display: flex">
            <div class="title" style="margin-top: 5px; margin-right: 10px">
              <p v-if="displaytype">Grid</p>
              <p v-if="!displaytype">List</p>
            </div>

            <label class="switch" >
              <input v-model="displaytype" type="checkbox" checked>
              <span class="slider round"></span>
            </label>
          </div>
        </div>
        <vs-divider></vs-divider>


        <vs-popup :active.sync="newListingPopup"
                  title="Create a new listing">
          <div class="new-listing-modal">
            <AddToCatalogue  />
          </div>
        </vs-popup>


          <div style="display: flex; margin-bottom: 1em;">
          <div style="display: flex;">
            <h2 class="title" style="margin-top: 10px; margin-right: 10px">Sort By: </h2>
            <select v-model="selected">
              <option disabled value="">Please select one</option>
              <option value="id">ID</option>
              <option value="name">Product Name</option>
              <option value="description">Description</option>
              <option value="recommendedRetailPrice">Recommended Retail Price</option>
              <option value="created">Date Created</option>
            </select>
            <vs-button @click="sortByName(null, selected, 0);" style="margin: 0 2em 0 0.5em; width: 100px">Sort</vs-button>
          </div>
            <!-- <vs-button @click="$router.push(`/businesses/${$route.params.id}/inventory`)" style="margin: 0 0.5em; width: 100px;">Inventory</vs-button>
                            <vs-button @click="$router.push(`/addtocatalogue`)" style="margin: 0 0.5em; width: 100px;">Add To Catalogue</vs-button>
                    -->
            <div style="margin: auto 0 auto auto">
              <div style="display: flex;">
                <vs-button @click="newListingPopup = true" class="header-button">New Product</vs-button>

                <ImageUpload :businessId=businessId :products=products style="margin: 0 0.5em; width: 100px;"/>
              </div>
            </div>
        </div>
        <div style="display: flex; margin-bottom: 1em;">
          <!-- If search query returns more than 10 products then this should be active -->
          <tfoot style="margin: auto 0 auto auto">
          <tr>
            <td class="displaying">Displaying {{searchRange[0]}}-{{searchRange[1]}} of {{filteredproducts.length}}</td>
            <div  v-if="filteredproducts.length > 10">
              <td><input class='row-md-2 prevNextSearchButton' type='button' @click="decreaseSearchRange()" value='Prev'/></td>
              <td><input class='row-md-2 prevNextSearchButton' type='button' @click="increaseSearchRange()" value='Next'/></td>
            </div>
          </tr>
          </tfoot>
        </div>


        <div v-if="displaytype">
          <div class="grid-container" style="margin: auto">
            
            <div style="position:relative" class="grid-item sub-container" v-for="product in filteredproducts.slice(productSearchIndexMin, productSearchIndexMax)"
                v-bind:href="product.id"
                :key="product.id">
              <div>
                <img v-if="product.primaryImagePath != null && isDevelopment()" style="width: 100%; height: 100%;   border-radius: 1em;" v-bind:src="require('../../../backend/src/main/resources/media/images/businesses/' + getImgUrl(product))"/>
                <img v-if="product.primaryImagePath != null && !isDevelopment()" style="width: 100%; height: 100%;   border-radius: 1em;" alt="Product Image" v-bind:src="getImgUrl(product)"/>
                <img v-if="product.primaryImagePath == null" style="width: 100%; height: 100%;   border-radius: 1em;" v-bind:src="require('../../public/ProductShoot.jpg')"/>
              </div>
              <div style="font-family: 'Ubuntu', sans-serif; font-size: 13pt; margin: 10px;  line-height: 1.5; display:flex; flex-direction: column;">
              
                <div style="display: flex;">
                  <p><a v-bind:href="'/products?id='+ product.id">{{ product.id }}</a></p>
                  <p style="margin-right: 0; margin-left: auto">{{ product.created }} </p>
                </div>
                <!-- Actions Dropdown -->
                <vs-dropdown vs-trigger-click class="actionButton">
                  <vs-button style="width: fit-content;">Actions</vs-button>
                  <vs-dropdown-menu>
                    <vs-dropdown-item @click="goToModify(); setProductToAlter(product.id, product.name, product.recommendedRetailPrice,
                          product.manufacturer, product.description)">
                      Modify product
                    </vs-dropdown-item>

                    <vs-dropdown-group vs-label="Change Primary Image" vs-collapse>
                      <vs-dropdown-item v-for="pImage in product.images" :key="pImage" @click="setPrimaryImage(product, pImage);">
                        {{pImage.name}}
                      </vs-dropdown-item>
                    </vs-dropdown-group>

                    <vs-dropdown-group vs-label="Delete An Image" vs-collapse>
                      <vs-dropdown-item v-for="pImage in product.images" :key="pImage" @click="deleteImage(product, pImage);">
                        {{pImage.name}}
                      </vs-dropdown-item>
                    </vs-dropdown-group>

                  </vs-dropdown-menu>
                </vs-dropdown>

                <p style="font-size: 20pt; font-weight: bold;  text-align: justify;">{{ product.name }} </p>
                <p style="font-size: 14pt; text-align: justify; margin-bottom: 20px;">{{ product.manufacturer }} </p>
                <p style="font-size: 15pt; margin-bottom: 35px">{{ product.description }} </p>
                <p style="color: #1F74FF; font-size: 25pt; font-weight: bold; position: absolute; bottom: 15px;" >{{currencySymbol + " " +  product.recommendedRetailPrice }} </p>
              </div>
            </div>
          </div>
        </div>


        <div v-if="!displaytype">
            <!-- Separate search within results search bar. Rather than calling the database, this filters the table
            entries within the page by matching the search field to the product's firstname, middlename or lastname -->
            <!-- When each heading is clicked, the sortByName() function is called, passing the json field name and a reference to the toggle array -->

            <vs-table :data="filteredproducts.slice(productSearchIndexMin, productSearchIndexMax)" style="border-spacing: 0px 20px; margin: 1em" search>
                <template slot="thead" style="background:blue">
                  <vs-th sort-key="id">
                      <div>ID</div>
              AddToCatalogue    </vs-th >
                  <vs-th sort-key="name">
                     <div>Product Name</div>
                  </vs-th>
                  <vs-th sort-key="description">
                    <div>Description</div>
                  </vs-th>
                  <vs-th sort-key="manufacturer">
                    <div>Manufacturer</div>
                  </vs-th>
                  <vs-th sort-key="recommendedRetailPrice">
                    <div>Recommended Retail Price</div>
                  </vs-th>
                  <vs-th sort-key="created">
                    <div>Date Created</div>
                  </vs-th>
                  <vs-th><!-- Actions Column --></vs-th>
                </template>

                <template slot-scope="{data}">
                  <vs-tr :key="product.id" v-for="product in data">
                    <vs-td style="width: 20px; padding-right: 10px">
                      <a v-bind:href="'/products?id='+ product.id">{{ product.id }}</a>
                      <div>
                        <img v-if="product.primaryImagePath != null && isDevelopment()" style="width: 100%; height: 100%;   border-radius: 1em;" v-bind:src="require('../../../backend/src/main/resources/media/images/businesses/' + getImgUrl(product))"/>
                        <img v-if="product.primaryImagePath != null && !isDevelopment()" style="width: 100%; height: 100%;   border-radius: 1em;" v-bind:src="getImgUrl(product)"/>
                        <img v-if="!product.primaryImagePath" style="width: 100%; height: 100%;   border-radius: 1em;" v-bind:src="require('../../public/ProductShoot.jpg')"/>
                      </div>
                    </vs-td>
                    <vs-td>{{ product.name }} </vs-td>
                    <vs-td>{{ product.description }} </vs-td>
                    <vs-td>{{ product.manufacturer }} </vs-td>
                    <vs-td style="text-align: center">{{currencySymbol + " " + product.recommendedRetailPrice }} </vs-td>
                    <td>{{ product.created }} </td>
                    <td>
                      <!-- Effectively repeated above, should refactor at some point. -->
                      <vs-dropdown vs-trigger-click>
                        <vs-button>Actions</vs-button>
                        <vs-dropdown-menu>
                          <vs-dropdown-item @click="goToModify(); setProductToAlter(product.id, product.name, product.recommendedRetailPrice,
                          product.manufacturer, product.description)">
                            Modify product
                          </vs-dropdown-item>

                          <vs-dropdown-group vs-label="Change Primary Image" vs-collapse>
                              <vs-dropdown-item v-for="pImage in product.images" :key="pImage" @click="setPrimaryImage(product, pImage);">
                                {{pImage.name}}
                              </vs-dropdown-item>
                          </vs-dropdown-group>

                          <vs-dropdown-group vs-label="Delete An Image" vs-collapse>
                              <vs-dropdown-item v-for="pImage in product.images" :key="pImage" @click="deleteImage(product, pImage);">
                                {{pImage.name}}
                              </vs-dropdown-item>
                          </vs-dropdown-group>

                        </vs-dropdown-menu>
                      </vs-dropdown>
                    </td>
                  </vs-tr>

                  <!-- If search query returns more than 10 products then this should be active -->
                  <tfoot v-if="filteredproducts.length > 1">
                  <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td class="displaying">Displaying {{searchRange[0]}}-{{searchRange[1]}} of {{filteredproducts.length}}</td>
                    <td><input class='row-md-2 prevNextSearchButton' type='button' @click="decreaseSearchRange()" value='Prev'/></td>
                    <td><input class='row-md-2 prevNextSearchButton' type='button' @click="increaseSearchRange()" value='Next'/></td>
                  </tr>
                  </tfoot>
                </template>
            </vs-table>
        </div>
      </div>
    <footer>
      "Product shoot" by Aameerule is licensed under CC BY 2.0
    </footer>
  </vs-card>
</template>

<script>
import api from "../Api";
import {store, mutations} from "../store";
import ImageUpload from "./ImageUpload";
import AddToCatalogue from "./AddToCatalogue";

import axios from "axios";
const Search = {
  name: "Search",

  components: {
    ImageUpload,
    AddToCatalogue
  },
  data: function() {
    return {
      newListingPopup: false,
      errors: [],
      toggle: [1,1,1,1,1],
      searchbar: "",
      searchbarResults: "",
      products: [],
      filteredproducts: [],
      reducedproducts: [],
      enableTable: false,
      resultTrack: "",
      productSearchIndexMin: 0,
      productSearchIndexMax: 10,
      business: null,
      businessId: null,
      displaytype: true,
      currencySymbol: "",
      currencyCode: "",
      selected: "",
      componentKey: 0,
    };
  },

  /**
   *
   * api.getBusinessProducts() queries the test back-end (json-server)
   * at /businesses/${businessId}/products which returns a JSON object list of test products which can
   * be filtered by the webpage.
 */
  mounted() {
    api.checkSession()
      .then((response) => {
        this.businessId = this.$route.params.id;
        this.userId = response.data.id;
        this.getUserInfo(this.userId);
        this.business = this.getBusinessName();

        api.getBusinessProducts(this.businessId)
            .then((response) => {
              this.$log.debug("Data loaded: ", response.data);
              this.products = response.data;
              this.filteredproducts = response.data;
            })
            .catch((error) => {
              this.$log.debug(error);
              this.error = "Failed to load products";
            })
            .finally(() => (this.loading = false));
      }).catch((error) => {
        this.$log.error(error);
    });
  },

  methods: {
    isDevelopment() {
      return (process.env.NODE_ENV === 'development')
    },

    setPrimaryImage(product, image) {
      this.imageId = image.id;
      this.productId = product.id;
      api.setPrimaryImage(this.businessId, this.productId, this.imageId)
          .then(() => {
            location.reload();
          }).catch((err) => {
            throw new Error(`Error trying to get user info from id: ${err}`);
      });
      this.$vs.notify({title:"Product image updated.", text:"New primary image successfully set.", color:"success"});
    },

    deleteImage(product, image) {
      this.imageId = image.id;
      this.productId = product.id;
      api.deletePrimaryImage(this.businessId, this.productId, this.imageId)
          .then(() => {
            location.reload();
            this.$vs.notify({title:"Product image deleted.", text:"Image successfully deleted.", color:"success"});
          }).catch((err) => {
            console.log(err);
      });
    },

    getImgUrl(product) {
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
    getUserInfo: function(userId) {
      if(store.loggedInUserId != null) {
        api.getUserFromID(userId) //Get user data
            .then((response) => {
              this.user = response.data;
              this.setCurrency(this.user.homeAddress.country);
            }).catch((err) => {
          throw new Error(`Error trying to get user info from id: ${err}`);
        });
      } else {
        this.$router.push({path: "/login"}); //If user not logged in send to login page
      }
    },

    setCurrency: function (country) {
      axios.get(`https://restcountries.eu/rest/v2/name/${country}`)
          .then( response => {
            this.currencySymbol = response.data[0].currencies[0].symbol;
            this.currencyCode = response.data[0].currencies[0].code;
          }).catch( err => {
        console.log("Error with getting cities from REST Countries." + err);
      });
    },

    getBusinessID: function () {
      return store.actingAsBusinessId
    },

    getBusinessName: function () {
      return store.actingAsBusinessName
    },

    //sets the product to alter id
    setProductToAlter(productId, productName, productRecommendedRetailPrice, productManufacturer, productDescription) {
      mutations.setProductToAlter(productId, productName, productRecommendedRetailPrice, productManufacturer, productDescription);
    },
    //modifies selected catalog item
    goToModify: function () {
      this.$router.push({path: `/businesses/${store.actingAsBusinessId}/products/modify`})
    },

    /**
     * Searches for the products in the database by calling the API function with an SQL query to find the
     * products based on the input in the search box.
     */
    searchProducts: function () {
      this.productSearchIndexMin = 0;
      this.productSearchIndexMax = 10;
      if (this.searchbar.length > 0) {
        this.enableTable = true;
        this.resultTrack = this.searchbar;
        api
            .searchQuery(this.searchbar)
            .then((response) => {
              this.$log.debug("Data loaded: ", response.data);
              this.products = response.data;
              this.filteredproducts = response.data;
            })
            .catch((error) => {
              this.$log.debug(error);
              this.error = "Failed to load products";
            })
            .finally(() => (this.loading = false));
      } else {
        this.errors.push("Please enter input the product you want to search for");
      }
    },

    /**
     * makes the checkproduct an administrator
     * if they are already, revoke privledges...
     */

    toggleAdmin: function (currentproduct) {
      if (currentproduct.role == 'product') {
        //currentproduct.id = true;
        api.makeproductAdmin(currentproduct.id);
        currentproduct.role = 'GAA'
      } else if (currentproduct.role == 'GAA') {
        api.revokeproductAdmin(currentproduct.id);
        currentproduct.role = 'product'
      }
    },

    /**
     * Filters the displayed products alphabetically by
     * @param JSONField is the name of the field to sort by (as string)
     * @param index references the toggle state list in the data object (int)
     */
    sortByName: function (event, JSONField) {
      const indexarray = ["id", "name", "description", "recommendedRetailPrice", "created"];

      //toggles the classlist (arrow up or down) in the child DOM element: <i/>
      if(event) {
        if(event.target.firstElementChild) {
          event.target.firstElementChild.classList.toggle('fa-angle-double-down');
          event.target.firstElementChild.classList.toggle('fa-angle-double-up');
        }
      }

      if (this.filteredproducts) {
        this.filteredproducts.sort(function(a, b) {
          var aField = a[JSONField];
          var bField = b[JSONField];

          //first check if null
          if(aField == null) {
            return 1;
          }
          if(bField == null) {
            return -1;
          }

          //check if a or b contains any numbers or whitespace
          //before capitalzation to avoid errors
          //also check if boolean
          if (!(typeof aField === "boolean" || typeof bField === "boolean")) {
            if ( !(/[^a-zA-Z]/.test(aField) && (/[^a-zA-Z]/.test(bField))) ) {
              aField = aField.toUpperCase();
              bField = bField.toUpperCase();
            }
          }


          //first < second
          if (aField > bField ) {
            return 1;
          }
          //second < first
          if (aField< bField) {
            return -1;
          }
          // a must be equal to b
          return 0;
        });

        let index = indexarray.indexOf(JSONField);

        if (index > 0) {
          if (this.toggle[index]) {
            this.filteredproducts.reverse();
            this.toggle[index]=0;
          } else {
            this.toggle[index]=1;
          }
        }
      }
    },

    /**
     * Filters the SQL query results to be displayed on this webpage.
     */
    filterproducts: function () {
      if (this.searchbar && this.resultTrack == this.searchbar) {
        this.filteredproducts = this.products.filter((item) => {
          return (item.firstName + " " + item.middleName + " " + item.lastName).includes(this.searchbar) || (item.firstName + " " + item.lastName).includes(this.searchbar);
        });

      }
    },



    /**
     * Helper function to control the number of search range values.
     * It increments Minimum and Maximum search index by 10 as long as the maximum search index does not exceed
     * the number of filtered products.
     */
    increaseSearchRange: function () {
      //Stop value from going over range
      if(this.productSearchIndexMax < this.filteredproducts.length) {
        //console.log(this.productSearchIndexMax, this.filteredproducts.length, this.productSearchIndexMin)
        let minMaxDiff = this.productSearchIndexMax - this.productSearchIndexMin;
        this.productSearchIndexMin += minMaxDiff;
        this.productSearchIndexMax += minMaxDiff;
      }
    },

    /**
     * Helper function to control the number of search range values.
     * It decrements Minimum and Maximum search index by 10 as long as the minimum is at least 10.
     */
    decreaseSearchRange: function () {
      //Stop value from reaching negative
      if(this.productSearchIndexMin >= 1) {
        let minMaxDiff = this.productSearchIndexMax - this.productSearchIndexMin;
        this.productSearchIndexMin -= minMaxDiff;
        this.productSearchIndexMax -= minMaxDiff;
      }
    }

  },

  /**
   * Closes the new product modal, and reset error fields.
   */
  closeNewListing: function() {
    this.newListingPopup = false;
    //Object.values(this.newListingErrors).forEach(input => input.error = false);
  },

  computed: {
    /**
     * Computes ranges to be displayed below table, max of range is switched
     * to length of product query if the length is less than the current range
     * @returns {Array} Array with start index and end index for searchRange
     */
    searchRange: function () {
      let max = this.productSearchIndexMax;

      if (max > this.filteredproducts.length) {
        max = this.filteredproducts.length
      }

      return [this.productSearchIndexMin + 1, max]
    }
  }
}

export default Search;
</script>

<style scoped>

.title {
  color: #1F74FF;
  font-weight: bold;
  font-size: 23px;
}

.main {
  background-color: white;
  width: 90%;
  margin: 1em auto;
}

.prevNextSearchButton {
  cursor: pointer;
  border-radius: 5em;
  color: #fff;
  background: #1F74FF;
  border: 0;
  padding: 10px 40px;
  margin-left: 35%;
  font-size: 13px;
  box-shadow: 0 0 20px 1px rgba(0, 0, 0, 0.04);
}

.displaying {
  text-align: right;
}


.profile-text-inner {
  margin: 2em auto;
  width: 90%;
  height: 80%;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, 350px);
  grid-column-gap: 2em;
  justify-content: center;

  padding: 10px;
  margin: 50px auto auto 0;
}
.grid-item {
  border: 1px solid rgba(0, 0, 0, 0.1);
  font-size: 30px;
  text-align: left;
  margin: 10px;
  width: 350px;
}
.sub-container {
  padding: 0.75em;
  border-radius: 1.5em;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #F5F5F5;
}


.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}

th {
  background: #1F74FF;
  color: white;
}

.actionButton {
  text-align: left;
}

</style>