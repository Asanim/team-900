import { mount, shallowMount } from '@vue/test-utils';
import Homepage from '../components/Homepage.vue';

let wrapper;

const mockUser = {
    "id": 5,
    "firstName": "Rayna",
    "middleName": "YEP",
    "lastName": "Dalgety",
    "nickname": "Universal",
    "bio": "zero tolerance task-force",
    "email": "rdalgety3@ocn.ne.jp",
    "dateOfBirth": "2006-03-30",
    "phoneNumber": "+7 684 622 5902",
    "homeAddress": "44 Ramsey Court",
    "created": "2021-04-05 00:11:04",
    "role": "USER",
    "businessesAdministered": [
        2
    ]
}

// Mocking $store
const store = {
    loggedInUserId: 5
};

const getLoggedInUserIdMethod = jest.spyOn(Homepage.methods, 'getLoggedInUserId');
getLoggedInUserIdMethod.mockResolvedValue(store.loggedInUserId);

beforeEach(() => {
    wrapper = shallowMount(Homepage, {
        propsData: {},
        mocks: {store},
        stubs: ['router-link', 'router-view'],
        methods: {},
    });
    wrapper.setData({userFirstName: mockUser.firstName})

    const getUserMethod = jest.spyOn(Homepage.methods, 'getUserDetails');
    getUserMethod.mockResolvedValue(mockUser);

    expect(wrapper).toBeTruthy();
});

afterEach(() => {
    wrapper.destroy();
});

describe('Homepage tests', () => {
    beforeEach(() => {
        wrapper.vm.userLoggedIn = true;
    });

    test('User\'s first name is shown', () => {
        const nameTitle = wrapper.find("#pageTitle")

        expect(nameTitle.text().includes(wrapper.vm.userFirstName)).toBe(true);
    });


    test('Go to profile gets called when clicked', () => {
        const profileButton = wrapper.find("#profileLink");
        wrapper.vm.goToProfilePage = jest.fn();

        profileButton.trigger('click')

        expect(wrapper.vm.goToProfilePage).toBeCalled();
    })
});

describe('Home page tests without user in state', () => {

    test('Page not loaded if no user logged in', () => {
        expect(wrapper.find("#body").exists()).toBe(false)
    })
})
