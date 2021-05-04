import { shallowMount, createLocalVue } from '@vue/test-utils';
import Business from '../components/BusinessRegister';
import Vuesax from 'vuesax';
import {store} from "../store";
import Vue from "vue";

let wrapper;
const localVue = createLocalVue();
localVue.use(Vuesax);

let $vs = {
    notify: jest.fn()
}

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
    "created": "2021-04-05 00:11:04",
    "role": "USER",
    "businessesAdministered": []
}

const $router = {
    push: jest.fn()
}
const checkAgeMethod = jest.spyOn(Business.methods, 'checkAge');
beforeEach(() => {
    wrapper = shallowMount(Business, {
        propsData: {},
        mocks: {$vs, store, $router},
        stubs: [],
        methods: {},
        localVue,
    });
});

afterEach(() => {
    wrapper.destroy();
});

//TESTS TO CHECK LOGIN ERROR HANDLING
describe('Business Register error checking', () => {


    beforeEach(() => {
        checkAgeMethod.mockImplementation(() => {
            return true;
        });
    });

    test('Handles empty Register', () => {
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(3);
    });

    test('Handles only name', () => {
        wrapper.vm.businessName = 'bestBusiness';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(2);
    })
    test('Handles only a type', () => {
        wrapper.vm.businessType = 'Charitable Organisation';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(2);
    })

    test('Handles old enough user', () => {
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(3);
    })
});

describe('Business Register user age checking', () => {
    beforeEach(() => {
        checkAgeMethod.mockImplementation(() => {
            return false;
        });
    });

    test('Handles to young user', () => {
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.includes('dob')).toBe(true);
    })
});

