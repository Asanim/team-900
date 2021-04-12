<template>
  <div class="card" id="body">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <form class="profile">
      <div class="profile-text-inner">
        <h3 class="title text-center">Search for users</h3>
        <div class="form-row">
          <div class="form-group col-md-6">
            <input type="search" class="form-control" placeholder="Search" name="searchbar" v-model="searchbar">
          </div>
          <div class="form-group col-md-6">
            <button type="button" class="searchButton" @click="searchUsers(); filterUsers()">Search!</button>
          </div>
        </div>
      </div>


      <div v-if="(this.searchbar.length > 0) && (this.enableTable)">
        <!-- Separate search within results search bar. Rather than calling the database, this filters the table
        entries within the page by matching the search field to the user's firstname, middlename or lastname -->
        <!-- When each heading is clicked, the sortByName() function is called, passing the json field name and a reference to the toggle array -->

        <table class="profile-text-inner" style="border-spacing: 0px 50px">
        <tr>
          <th>
            <button type="button" class="row-md-2 headingButton" @click="sortByName($event, 'id', 0);">
              ID <i class="fa fa-angle-double-down" style="font-size:20px"/>
            </button>
          </th>
          <th>
            <button type="button" class="row-md-2 headingButton" @click="sortByName($event, 'firstName', 0);">
              Firstname<i class="fa fa-angle-double-down" style="font-size:20px"/>
            </button>
          </th>
          <th>
            <button type="button" class="row-md-2 headingButton" @click="sortByName($event, 'middleName', 1);">
              Middlename<i class="fa fa-angle-double-down" style="font-size:20px"/>
            </button>
          </th>
          <th>
            <button type="button" class="row-md-2 headingButton" @click="sortByName($event, 'lastName', 2)">
              Lastname<i class="fa fa-angle-double-down" style="font-size:20px"/>
            </button>
          </th>
          <th>
            <button type="button" class="row-md-2 headingButton" @click="sortByName($event, 'homeAddress', 3)">
              Address<i class="fa fa-angle-double-down" style="font-size:20px"/>
            </button>
          </th>
          <th>
            <button type="button" class="row-md-2 headingButton" @click="sortByName($event, 'email', 4)">
              Email<i class="fa fa-angle-double-down" style="font-size:20px"/>
            </button>
          </th>

          <th  v-if="isDGAA">
            <button type="button" class="row-md-2 headingButton" @click="sortByName($event, 'isAdmin', 4)">
              Is Admin<i class="fa fa-angle-double-down" style="font-size:20px"/>
            </button>
          </th>

          <th  v-if="isDGAA">
            <button type="button" class="row-md-2 headingButton" @click="sortByName($event, 'isAdmin', 4)">
              Toggle Admin<i class="fa fa-angle-double-down" style="font-size:20px"/>
            </button>
          </th>

        </tr>


        <tr v-for="user in users.slice(userSearchIndexMin, userSearchIndexMax)"
            v-bind:href="user.id"
            :key="user.id">
          <td><a v-bind:href="'/Users?id='+ user.id">{{ user.id }}</a></td>
          <td>{{ user.firstName }} </td>
          <td> {{ user.middleName }} </td>
          <td> {{ user.lastName }} </td>
          <td> {{ user.homeAddress }} </td>
          <td>{{ user.email }}</td>
          <td v-if="isDGAA" >{{ user.role }}</td>
          <td v-if="isDGAA">
            <input type="checkbox" @click="toggleAdmin(user)">
          </td>
        </tr>

        <!-- If search query returns more than 10 users then this should be active -->
        <tfoot v-if="filteredUsers.length > 10">
          <tr>
            <td class="displaying">Displaying {{searchRange[0]}}-{{searchRange[1]}} of {{filteredUsers.length}}</td>
            <td><input class='row-md-2 prevNextSearchButton' type='button' @click="decreaseSearchRange()" value='Prev'/></td>
            <td><input class='row-md-2 prevNextSearchButton' type='button' @click="increaseSearchRange()" value='Next'/></td>
          </tr>
        </tfoot>
      </table>
      </div>

    </form>

  </div>
</template>

<script>
import api from "../Api";

const Search = {
  name: "Search",
  data: function() {
    return {
      errors: [],
      toggle: [1,1,1,1,1,1],
      searchbar: "",
      searchbarResults: "",
      users: [],
      filteredUsers: [],
      reducedUsers: [],
      enableTable: false,
      resultTrack: "",
      userSearchIndexMin: 0,
      userSearchIndexMax: 10,
      isDGAA: false
    };
  },

  /**
   * populates the page with with dummy data for testing purposes only
   *
   * api.getSearchResults() queries the test back-end (json-server)
   * at /users which returns a JSON object list of test users which can
   * be filtered by the webpage.
   *
   * remove when test back end works well...
 */
  mounted() {
    console.log(this.$store.state.userRole)
    console.log(this.$store.state.userId)
    console.log(this.$store.state.viewingUserId)

    if (this.$store.state.userRole == 'DGAA') {
      this.isDGAA = true;
    }
  },


  methods: {
    /**
     * Searches for the users in the database by calling the API function with an SQL query to find the
     * users based on the input in the search box.
     */
    searchUsers: function () {
      this.userSearchIndexMin = 0;
      this.userSearchIndexMax = 10;
      if (this.searchbar.length > 0) {
        this.enableTable = true;
        this.resultTrack = this.searchbar;
        console.log(this.searchbar);
        api
            .searchQuery(this.searchbar)
            .then((response) => {
              console.log(response.data);
              this.$log.debug("Data loaded: ", response.data);
              this.users = response.data;
            })
            .catch((error) => {
              this.$log.debug(error);
              this.error = "Failed to load users";
            })
            .finally(() => (this.loading = false));
      } else {
        this.errors.push("Please enter input the user you want to search for");
      }
    },

    /**
     * makes the checkuser an administrator
     * if they are already, revoke privledges...
     */

    toggleAdmin: function (currentUser) {
      if (currentUser.role == 'USER') {
        //currentUser.id = true;
        api.makeUserAdmin(currentUser.id);
        currentUser.role = 'GAA'
        //console.log("admin true"+currentUser.id+currentUser.firstName)
      } else if (currentUser.role == 'GAA') {
        api.revokeUserAdmin(currentUser.id);
        currentUser.role = 'USER'
      }
    },

    /**
     * Filters the displayed users alphabetically by
     * @param JSONField is the name of the field to sort by (as string)
     * @param index references the toggle state list in the data object (int)
     */
    sortByName: function (event, JSONField, index) {
      //toggles the classlist (arrow up or down) in the child DOM element: <i/>
      if(event.target.firstElementChild) {
        event.target.firstElementChild.classList.toggle('fa-angle-double-down');
        event.target.firstElementChild.classList.toggle('fa-angle-double-up');
      }

      if (this.filteredUsers) {
        this.filteredUsers.sort(function(a, b) {

          var aField = a[JSONField];
          var bField = b[JSONField];

          //first check if a or b contains any numbers or whitespace
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
        if (this.toggle[index]) {
          this.filteredUsers.reverse();
          this.toggle[index]=0;
        } else {
          this.toggle[index]=1;
        }
      }
    },

    /**
     * Filters the SQL query results to be displayed on this webpage.
     */
    filterUsers: function () {
      if (this.searchbar && this.resultTrack == this.searchbar) {
        this.filteredUsers = this.users.filter((item) => {
          return (item.firstName + " " + item.middleName + " " + item.lastName).includes(this.searchbar) || (item.firstName + " " + item.lastName).includes(this.searchbar);
        });

      }
    },



    /**
     * Helper function to control the number of search range values.
     * It increments Minimum and Maximum search index by 10 as long as the maximum search index does not exceed
     * the number of filtered users.
     */
    increaseSearchRange: function () {
      //Stop value from going over range
      if(this.userSearchIndexMax < this.filteredUsers.length) {
        //console.log(this.userSearchIndexMax, this.filteredUsers.length, this.userSearchIndexMin)
        this.userSearchIndexMin += 10;
        this.userSearchIndexMax += 10;
      }
    },

    /**
     * Helper function to control the number of search range values.
     * It decrements Minimum and Maximum search index by 10 as long as the minimum is at least 10.
     */
    decreaseSearchRange: function () {
      //Stop value from reaching negative
      if(this.userSearchIndexMin >= 10) {
        this.userSearchIndexMin -= 10;
        this.userSearchIndexMax -= 10;
      }
    }

  },
  computed: {
    /**
     * Computes ranges to be displayed below table, max of range is switched
     * to length of user query if the length is less than the current range
     * @returns {Array} Array with start index and end index for searchRange
     */
    searchRange: function () {
      let max = this.userSearchIndexMax;

      if (max > this.filteredUsers.length) {
        max = this.filteredUsers.length
      }

      return [this.userSearchIndexMin + 1, max]
    }
  }
}

export default Search;
</script>

<style scoped>
.title {
  padding-top: 40px;
  color: #8C55AA;
  font-family: 'Ubuntu', sans-serif;
  font-weight: bold;
  font-size: 23px;
}

.card {
  background-color: white;
}

.searchButton {
  cursor: pointer;
  border-radius: 5em;
  color: #fff;
  background: linear-gradient(to right, #9C27B0, #E040FB);
  border: 0;
  padding-left: 40px;
  padding-right: 40px;
  padding-bottom: 10px;
  padding-top: 10px;
  font-family: 'Ubuntu', sans-serif;
  margin-left: 35%;
  font-size: 13px;
  box-shadow: 0 0 20px 1px rgba(0, 0, 0, 0.04);
}

.prevNextSearchButton {
  cursor: pointer;
  border-radius: 5em;
  color: #fff;
  background: linear-gradient(to right, #9C27B0, #E040FB);
  border: 0;
  padding-left: 20px;
  padding-right: 20px;
  padding-bottom: 5px;
  padding-top: 5px;
  font-family: 'Ubuntu', sans-serif;
  margin-top: 10px;
  font-size: 13px;
  box-shadow: 0 0 20px 1px rgba(0, 0, 0, 0.04);
}

.displaying {
  padding-top: 15px;
}


.headingButton {
  cursor: pointer;
  border-radius: 5em;
  color: #fff;
  background: linear-gradient(to right, #9C27B0, #E040FB);
  border: 0;
  padding-left: 10px;
  padding-right: 10px;
  padding-bottom: 5px;
  padding-top: 5px;
  font-family: 'Ubuntu', sans-serif;
  font-size: 13px;
  box-shadow: 0 0 20px 1px rgba(0, 0, 0, 0.04);
}

.profile-text-inner {
  margin: 7em auto;
  width: 90%;
  height: 80%;
}

</style>