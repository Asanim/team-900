/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

/**
 * Declare all available services here
 */
import axios from 'axios'

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const instance = axios.create({  
  baseURL: SERVER_URL,
  timeout: 1000  
});


export default {
    /**
     * Attempt to authenticate a user account with a username and password
     * @param username The user's username input
     * @param password The user's password input
     * @param token Authentication token added to API request header
     * @returns {Promise<AxiosResponse<any>>}
     */
    login: (email, password) => instance.post('login', {email, password}, {withCredentials: true}),

    logout: () => instance.post('logoutuser', [], {withCredentials: true}),

    checkSession: () => instance.get('checksession', {withCredentials: true}),

    checkBusinessSession: () => instance.get('checkbusinesssession', {withCredentials: true}),

  // user POST create new user account data
    /**
     * Create a new user by storing their data to the database
     * @param firstName Their firstname
     * @param middleName Their middlename (OPTIONAL)
     * @param lastName Their lastname
     * @param nickname Their nickname (OPTIONAL)
     * @param bio Their bio (OPTIONAL)
     * @param email Their email
     * @param dateOfBirth Their date of birth
     * @param phoneNumber Their phone
     * @param homeAddress Their home address
     * @param password Their password hashed using password-hash library
     * @param registerDate Their registration date
     * @returns {Promise<AxiosResponse<any>>}
     */
    createUser: async(firstName, middleName, lastName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password) =>
  instance.post('users', {firstName, middleName, lastName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password}),

    /**
     * Get a specific user via their unique ID number
     * @param userId The user's unique ID number
     * @returns {Promise<AxiosResponse<any>>}
     */
    getUserFromID: (userId) => instance.get(`users/${userId}`, {withCredentials: true}),

    /**
     * Get all users from the database.
     * @returns {Promise<AxiosResponse<any>>}
     */
    getAll: () => instance.get('Users', {
        transformResponse: [function (data) {
            return data? JSON.parse(data)._embedded.students : data;
        }]
    }),

    /**
     * Search for one or more users from the database from a given input.
     * @param input Any information relating to the user. (e.g: firstname, lastname, ...etc)
     * @returns {Promise<AxiosResponse<any>>}
     */
    //searchQuery: async(input) => instance.post('users/search', input),

    /**
     * Query search results that uses searchQuery function
     * @returns {Promise<AxiosResponse<any>>}
     */
    searchQuery: (query) => instance.get(`/users/search?searchQuery="${query}"`,{withCredentials: true}),

    /**
     * Method (frontend) to let a DGAA user make a user an GAA admin user.
     * @param id user id to be made admin.
     */
    makeUserAdmin: async(id) =>
        instance.put('/users/'+id+'/makeAdmin',{},{withCredentials: true}),

    /**
     * Method (frontend) to let a DGAA user revoke GAA admin status from another user. Reverts the user back to USER role.
     * @param id user id to revoke admin user role.
     */
    revokeUserAdmin: async(id) =>
        instance.put('/users/'+id+'/revokeAdmin',{}, {withCredentials: true}),


    // ------ BUSINESSES

    /**
     * Create a new business by storin their data in the database
     * @param name business name
     * @param description business description
     * @param address business address
     * @param businessType business type
     */
    createBusiness: async(name, description, address, businessType) =>
    instance.post('businesses', {name, description, address, businessType}, {withCredentials: true}),

    /**
     * Retrieve a single business with their unique id.
     * @param businessId the unique id of the business.
     */
    actAsBusiness: async(businessId) =>
        instance.post('actasbusiness',{businessId}, {withCredentials: true}),

    /**
     * Retrieve a single business with their unique id.
     * @param businessId the unique id of the business.
     * @returns {Promise<AxiosResponse<any>>} a business json containing relevant information.
     */
    getBusinessFromId: (businessId) => instance.get(`businesses/${businessId}`, {withCredentials: true}),

    /**
     * Put request to make a user a business administrator (not primary).
     * @param businessId unique identifier of the business.
     * @param userId unique identifier of the user.
     * @returns {Promise<AxiosResponse<any>>} a response with a OK if it is successful.
     */
    makeUserBusinessAdmin: (businessId, userId) => instance.put(`businesses/${businessId}/makeAdministrator`, {userId}, {withCredentials: true}),

    /**
     * Put request to remove administrator rights to a business.
     * @param businessId business identifier to remove rights to.
     * @param userId user identifier to remove the rights from.
     * @returns {Promise<AxiosResponse<any>>} a response with appropriate status code.
     */
    removeUserAsBusinessAdmin: (businessId, userId) => instance.put(`businesses/${businessId}/removeAdministrator`, {userId}, {withCredentials: true}),

    /**
     * Create a new product.
     * @param businessId id of the business to
     * @param id product id (chosen by user)
     * @param name product name
     * @param description product description
     * @param manufacturer manufacturer of this product.
     * @param recommendedRetailPrice product recommended retail price in their local currency
     */
    createProduct: async(businessId, id, name, description, manufacturer, recommendedRetailPrice) =>
        instance.post(`/businesses/${businessId}/products`, {businessId, id, name, description, manufacturer, recommendedRetailPrice}, {withCredentials: true}),

    /**
     * Adds a new inventory to the current business
     * @param businessId id of the business to
     * @param productId product id (chosen by user)
     * @param quantity quantity of the product
     * @param pricePerItem price per one item
     * @param totalPrice total price (It is not pricePerItem times some number)
     * @param manufactured Manufacture datee
     * @param sellBy To be sold beforee this date
     * @param bestBefore To be consumed before this date
     * @param expires product will expire on this date
     */
    createInventory: async(businessId, productId, quantity, pricePerItem, totalPrice, manufactured, sellBy, bestBefore, expires) =>
        instance.post(`/businesses/${businessId}/inventory`, {productId, quantity, pricePerItem, totalPrice, manufactured, sellBy, bestBefore, expires}, {withCredentials: true}),

    /**
     * Obtains the inventory of the said business from the database
     * @param businessId the id of the said business
     */
    getInventory: async(businessId) =>
        instance.get(`/businesses/${businessId}/inventory`, {withCredentials: true}),

    /**
     * Adds a new listing for the said business' marketplace
     * @param businessId the id of the said business
     * @param inventoryItemId the id of the inventory item
     * @param quantity the quantity of the products
     * @param price the total price of the listing
     * @param moreInfo OPTIONAL: Info about listing
     * @param closes Closing date
     */
    createListing: async(businessId, inventoryItemId, quantity, price, moreInfo, closes) =>
        instance.post(`/businesses/${businessId}/listings`, {inventoryItemId, quantity, price, moreInfo, closes}, {withCredentials: true}),

    /**
     * modifies catalog product
     * @param id product id (chosen by user)
     * @param name product name
     * @param description product description
     * @param manufacturer manufacturer of this product.
     * @param recommendedRetailPrice product recommended retail price in their local currency
     */
    modifyProduct: async (businessId, productId, id, name, description, manufacturer, recommendedRetailPrice) =>
        instance.put(`/businesses/${businessId}/products/${productId}`, {businessId, productId, id, name, description, manufacturer, recommendedRetailPrice}, {withCredentials: true}),

    /**
     * Get request to return products owned by a business.
     * @param businessId
     * @returns {Promise<AxiosResponse<any>>}
     */
    getBusinessProducts: (businessId) => instance.get(`businesses/${businessId}/products`,  {withCredentials: true}),

    /**
     * Post request to send a product image
     * @param businessId business identifier to remove rights to.
     * @param productId product identifier, product that the image is for
     * @param image FormData object containing image file to be sent to server
     * @returns {Promise<AxiosResponse<any>>} a response with appropriate status code.
     */
    postProductImage: (businessId, productId, image) => instance.post(`businesses/${businessId}/products/${productId}/images`, image, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      withCredentials: true,
    }),

    /**
     * Put request to set the primary image from the existing images for a product.
     * @param businessId
     * @param productId
     * @param imageId
     * @returns {Promise<AxiosResponse<any>>} A response with appropriate status code.
     */
    setPrimaryImage: (businessId, productId, imageId) => instance.put(`businesses/${businessId}/products/${productId}/images/${imageId}/makeprimary`, "", {withCredentials: true}),

    /**
     * Delete request to remove product image.
     * @param businessId
     * @param productId
     * @param imageId
     * @returns {Promise<AxiosResponse<any>>} A response with appropriate status code.
     */
    deletePrimaryImage: (businessId, productId, imageId) => instance.delete(`businesses/${businessId}/products/${productId}/images/${imageId}`, {withCredentials: true}),

    // === BUSINESS INVENTORY LISTINGS

    /**
     * Retrieves a business' sale listings.
     * @param businessId Id of business.
     * @returns {Promise<AxiosResponse<any>>} 200 with (a potentially empty) array of listings. 401, 406 otherwise.
     */
    getBusinessListings: (businessId) => instance.get(`/businesses/${businessId}/listings`, {withCredentials: true}),

    getBusinessInventory: (businessId) => instance.get(`/businesses/${businessId}/inventory`, {withCredentials: true})
}