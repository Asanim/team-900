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

package org.seng302;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.models.*;
import org.seng302.repositories.BusinessRepository;
import org.seng302.repositories.ProductRepository;
import org.seng302.repositories.UserRepository;
import org.seng302.utilities.SchedAdminCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * This spring component runs at application startup to do some initialisation
 * work.
 */
@Component
public class MainApplicationRunner implements ApplicationRunner {

  private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());
  private UserRepository userRepository;
  private BusinessRepository businessRepository;
  @Autowired private ProductRepository productRepository;

  private SchedAdminCheck schedAdminCheck;


  /**
   * This constructor is implicitly called by Spring (purpose of the @Autowired
   * annotation). Injected constructors can be supplied with instances of other
   * classes (i.e. dependency injection)
   *
   * @param userRepository the user repository.
   */
  @Autowired
  public MainApplicationRunner(UserRepository userRepository, BusinessRepository businessRepository, SchedAdminCheck schedAdminCheck) {
    this.userRepository = userRepository;
    this.businessRepository = businessRepository;
    this.schedAdminCheck = schedAdminCheck;
  }

  /**
   * By overriding the run method, we tell Spring to run this code at startup. See
   * https://dzone.com/articles/spring-boot-applicationrunner-and-commandlinerunne
   */
  @Override
  public void run(ApplicationArguments args) throws Exception {
    logger.info("Startup application with {}", args);
    schedAdminCheck.fixedRateSched();

   // Test data for our user
    Address address = new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080");
    User u1 = new User("Wileen", "YEP", "Tilsley","Diverse", "hybrid orchestration","wtilsley0@rakuten.co.jp","1921-10-08","+86 815 603 3959", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"), "zWkb3AeLn3lc");
    User u11 = new User("Wileen", "YEP", "Backgammon","Diverse", "hybrid orchestration","wileenbackgammon@rakuten.co.jp","1921-10-08","+86 815 603 3959", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"), "asda3142sadf");
    User u2 = new User("Gannon", "YEP", "Tynemouth", "Exclusive", "6th generation intranet", "gtynemouth1@indiatimes.com","1996-03-31","+62 140 282 1784", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"HGD0nAJNjSD");
    User u3 = new User("Leticia", "YEP", "Semorad","Synergistic", "dynamic paradigm", "lsemorad2@flavors.me","1978-06-08","+86 485 752 9506", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"Tkzr7qcTOQpk");
    User u4 = new User("Rayna", "YEP", "Dalgety", "Universal", "zero tolerance task-force" , "rdalgety3@ocn.ne.jp","2006-03-30","+7 684 622 5902", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ATQWJM");
    User u5 = new User("Edyth", "YEP", "Dory","Balanced", "solution-oriented info-mediaries", "edory4@163.com","1997-04-10","+255 972 389 7556", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"8pUKhQV0bgg1");
    User u6 = new User("Raquela", "YEP", "Haylands", "Future-proofed", "24/7 workforce", "rhaylands5@shutterfly.com","2004-10-06","+86 944 435 8212", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"zfKjrTU1");
    User u7 = new User("Dynah", "YEP", "Goter", "Profound", "responsive solution", "dgoter6@nasa.gov","1994-02-06","+33 271 150 3057", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"XQx7szwPR");
    User u8 = new User("Joete", "YEP", "Stopps", "Multi-layered","responsive capacity","jstopps7@flickr.com","1984-10-14","+36 694 564 9090", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"KlbUWrQ");
    User u9 = new User("Elwood", "YEP", "Altamirano", "Visionary", "mobile capacity", "ealtamirano8@phpbb.com","1927-02-28","+381 643 240 6530", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ItqVNvM2JBA");
    User u0 = new User("Roxy", "YEP", "Rossbrook", "Organic","multi-state algorithm", "rrossbrook9@xrea.com","1957-06-30","+86 966 207 4419", new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"JNXPLa");

    userRepository.save(u1);
    userRepository.save(u11);
    userRepository.save(u2);
    userRepository.save(u3);
    userRepository.save(u4);
    userRepository.save(u5);
    userRepository.save(u6);
    userRepository.save(u7);
    userRepository.save(u8);
    userRepository.save(u9);
    userRepository.save(u0);

    Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "Brazil","39960-000");
    Address a2 = new Address("620","Sutherland Lane","Dalai", null,"China", null);
    // Test data for businesses
    Business b1 = new Business("Business1", "Test Business 1", a1, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
    Business b2 = new Business("Business2", "Test Business 2", a2, BusinessType.RETAIL_TRADE);
    b1.createBusiness(u8);
    b2.createBusiness(u4);

    businessRepository.save(b1);
    businessRepository.save(b2);

    // Test Data for Products
    Product p1 = new Product("07-4957066", 1, "Spoon", "Soup, Plastic", 14.69, new Date());
    Product p2 = new Product("55-9986232", 1, "Lamb Leg", "Bone - In Nz", 43.66, new Date());
    Product p3 = new Product("55-9986232", 2, "Seedlings", "Buckwheat, Organic", 1.26, new Date());
    Product p4 = new Product("12-5088639", 2, "Foam Cup", "6 Oz", 55.2, new Date());

    productRepository.save(p1);
    productRepository.save(p2);
    productRepository.save(p3);
    productRepository.save(p4);

  }
}
