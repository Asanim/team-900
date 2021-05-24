<template>
  <div id="listings-container">
    <div :class="{'header-container-grid': !tableView, 'header-container-table': tableView}">
      <div v-show="!tableView" style="display: inline-flex">
        <vs-select @change="sortListingsByKey" id="header-sort" v-model="selectedSort" label="Sort">
          <vs-select-item v-for="sort in sortOptions" :key="sort" :value="sort" :text="sort"/>
        </vs-select>
        <vs-button id="sort-button"
                   type="line"
                   :icon="sortDirection === 'desc' ? 'expand_more' : 'expand_less'"
                   @click="changeSortDirection(); sortListingsByKey();"
                   icon-after>
          <span v-if="sortDirection === 'desc'">Descending</span>
          <span v-else>Ascending</span>
        </vs-button>
      </div>
      <!-- ====== LISTINGS OPTIONS MENU ===== -->
      <div id="view-switch">
        <div style="padding: 0 1em; font-size: 14px;"> View </div>
        <vs-switch id="table-switch" v-model="tableView" style="margin: auto 0; width: 50px">
          <span slot="on">Table</span>
          <span slot="off">Grid</span>
        </vs-switch>
      </div>
    </div>
    <vs-divider></vs-divider>

    <!-- ===== GRID VIEW ===== -->
    <div v-if="!tableView" id="grid-view">
      <vs-card class="listing-card" v-for="listing in listings" :key="listing.id" :fixed-height="true">
        <div style="margin: 2px 4px; font-size: 12px; font-weight: bold">{{ listing.productName }}</div>
        <div class="listing-header">
          <img alt="Product Image" v-if="listing.inventoryItem.product.primaryImagePath != null && isDevelopment()" class="image" v-bind:src="require('../../../backend/src/main/resources/media/images/businesses/' + getImgUrl(listing.inventoryItem.product))"/>
          <img alt="Product Image" v-if="listing.inventoryItem.product.primaryImagePath != null && !isDevelopment()" class="image" v-bind:src="getImgUrl(listing.inventoryItem.product)"/>
          <img alt="Product Image" v-if="!listing.inventoryItem.product.primaryImagePath" class="image" src="ProductShoot.jpg"/>
          <div style="font-size: 14px; padding-left: 4px; margin: auto 0;">
            <div>{{ currencySymbol }}{{ listing.price }}</div>
            <div>{{ listing.quantity }}x</div>
          </div>
        </div>

        <div style="font-size: 12px"> Closes: {{ listing.closes }}</div>
        <vs-divider style="margin-top: 0"></vs-divider>

        <div>{{ listing.moreInfo }}</div>
        <div slot="footer" class="grid-card-footer">
          Listed: {{ listing.created }}
        </div>
      </vs-card>
    </div>

    <!-- ===== TABLE VIEW ===== -->
    <div v-else id="table-view">
      <vs-table
          :data="listings"
          noDataText="This business has no listings."
          pagination
          stripe>
        <template slot="thead">
          <vs-th style="border-radius: 8px 0 0 0"> <!-- Product Image Thumbnail --> </vs-th>
          <vs-th sort-key="productName"> Product </vs-th>
          <vs-th sort-key="price"> Price </vs-th>
          <vs-th sort-key="quantity"> Qty </vs-th>
          <vs-th sort-key="closes"> Closes </vs-th>
          <vs-th sort-key="created"> Listed </vs-th>
          <vs-th style="border-radius: 0 8px 0 0"> Additional Info </vs-th>
        </template>
        <template slot-scope="{data}">
          <vs-tr v-for="listing in data" :key="listing.id">
            <vs-td>
              <img alt="Product Image" v-if="listing.inventoryItem.product.primaryImagePath != null && isDevelopment()" class="image" v-bind:src="require('../../../backend/src/main/resources/media/images/businesses/' + getImgUrl(listing.inventoryItem.product))"/>
              <img alt="Product Image" v-if="listing.inventoryItem.product.primaryImagePath != null && !isDevelopment()" class="image" v-bind:src="getImgUrl(listing.inventoryItem.product)"/>
              <img alt="Product Image" v-if="!listing.inventoryItem.product.primaryImagePath" class="image" src="ProductShoot.jpg"/>
            </vs-td>
            <vs-td>{{ listing.productName }}</vs-td>
            <vs-td>{{ currencySymbol }}{{ listing.price }}</vs-td>
            <vs-td>{{ listing.quantity }}x</vs-td>
            <vs-td>{{ listing.closes }}</vs-td>
            <vs-td>{{ listing.created }}</vs-td>
            <vs-td>{{ listing.moreInfo }}</vs-td>
          </vs-tr>
        </template>
      </vs-table>
    </div>

  </div>
</template>

<script>
import api from "../Api";
import axios from "axios";

export default {
  name: "BusinessListings",

  props: {
    businessId: Number,
    country: String,
  },

  data: function() {
    return {
      listings: [],
      tableView: false, // Default grid-card option.

      sortOptions: ["Listing Date", "Closing Date", "Product Name"], // Not implemented yet.
      selectedSort: null,
      sortDirection: "desc",

      currencySymbol: "$",
    }
  },

  methods: {
    /**
     * Retrieves the sale listings of this business.
     */
    getListings: function() {
      api.getBusinessListings(this.businessId)
        .then((res) => {
          this.listings = res.data;
          // Add product name to listing object to make it accessible for table to sort.
          this.listings = this.listings.map(listing => {
            listing.productName = listing.inventoryItem.product.name;
            return listing;
          })
        })
        .catch((error) => {
          this.$log.error(error);
        });
    },

    isDevelopment() {
      return (process.env.NODE_ENV === 'development')
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

    /**
     * Change sort direction to ascending/descending for grid view only.
     */
    changeSortDirection: function() {
      this.sortDirection = this.sortDirection === "desc" ? "asc" : "desc";
    },

    /**
     * Changes sort view of the listings for grid view only.
     */
    sortListingsByKey: function() {
      let selectedKey;
      let sortKeyDirection = this.sortDirection;
      if (this.selectedSort === "Closing Date") selectedKey = "closes";
      else if (this.selectedSort === "Product Name") selectedKey = "productName";
      else selectedKey = "created";

      this.listings.sort(function(a, b) {
          if (a[selectedKey] < b[selectedKey]) return sortKeyDirection === "desc" ? 1 : -1;
          if (a[selectedKey] > b[selectedKey]) return sortKeyDirection === "desc" ? -1 : 1;
          return 0;
      });
    },

    /**
     * Sets display currency based on the user's home country.
     */
    setCurrency: function (country) {
      axios.get(`https://restcountries.eu/rest/v2/name/${country}`)
          .then(response => {
            this.currencySymbol = response.data[0].currencies[0].symbol;
          }).catch(err => {
        this.$log.debug(err);
      });
    },
  },

  mounted: function() {
    this.getListings();
    this.setCurrency(this.country);
  },
}
</script>

<style scoped>
  #listings-container {
    display: flex;
    flex-direction: column;
    margin: 1em auto;
  }

  .header-container-grid {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
  }

  .header-container-table {
    padding-top: 25px;
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
  }

  #view-switch {
    display: flex;
    margin: auto 0 0.5em 0;
  }

  #sort-button {
    top: 5px;
    margin: auto auto 0 auto;
  }

  .image {
    min-width: 50px;
    max-width: 50px;
    max-height: 50px;

    border-radius: 4px;
    margin: 0 0 auto 0;
  }


  /* === LISTING CARD ==== */
  #grid-view {
    display: grid;
    grid-template-columns: repeat(auto-fit, 250px);
    justify-content: space-around;
  }

  #grid-view::after {
    content: "";
    flex: auto;
  }

  .listing-card {
    width: 225px;
    height: 225px;
    margin: 0.5em 1em;
  }

  .listing-header {
    display: flex;
    justify-content: flex-start;
    padding: 4px 0 0 0;
  }

  .grid-card-footer {
    font-size: 12px;
  }

  .listing-card >>> footer {
    margin-left: 0;
    margin-right: auto;
    right: auto;
  }

  @media screen and (max-width: 500px) {
    .header-container-grid {
      flex-direction: column;
    }

    #view-switch {
      margin-top: 0.5em;
    }
  }

</style>