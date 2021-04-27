import { shallowMount, createLocalVue } from '@vue/test-utils';
import AddToCatalogue from '../components/AddToCatalogue';
import Vuesax from 'vuesax';
import {store} from "../store";

//import {CurrencyInput} from "../components/CurrencyInput";
//import api from "../Api";

let wrapper;

const localVue = createLocalVue();
localVue.use(Vuesax);


// let $vs = {
//     notify: jest.fn()
// }

store.loggedInUserId = 5;

//Mock user
const mockUser = {
    "id": 5,
    "firstName": "Rayna",
    "middleName": "YEP",
    "lastName": "Dalgety",
    "nickname": "Universal",
    "bio": "zero tolerance task-force",
    "email": "rdalgety3@ocn.ne.jp",
    "dateOfBirth": "1999-02-28",
    "phoneNumber": "+7 684 622 5902",
    "homeAddress": "44 Ramsey Court",
}

// const mockProduct = {
//
// }



beforeEach(() => {
    wrapper = shallowMount(AddToCatalogue, {
        propsData: {},
        mocks: {store},
        stubs: ['router-link', 'router-view'],
        methods: {},
        // components: CurrencyInput,
        localVue,
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Component', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});



//TESTS TO CHECK LOGIN ERROR HANDLING
describe('Add To Catalogue form error checking', () => {


    beforeEach(() => {
        wrapper.vm.user = mockUser;
        // const createItemMethod = jest.spyOn(AddToCatalogue.methods, 'createItem');
        // createItemMethod.mockResolvedValue(mockUsers);
        //wrapper.vm.checkForm = jest.fn();
    });

    test('Handles empty Register', () => {
        const addBtn = wrapper.find('.add-button');
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(2);
    });

    test('Handles no product name', () => {
        wrapper.vm.productName = "";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "Good product";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.rrp = 22;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles no product id', () => {
        wrapper.vm. productName = "Big Tyre";
        wrapper.vm.productId = "";
        wrapper.vm.description = "Good product";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.rrp = 22;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles no description, manufacturer or rrp', () => {
        wrapper.vm. productName = "Big Tyre";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "";
        wrapper.vm.manufacturer = "";
        wrapper.vm.rrp = null;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(0);
    });
});

