package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.DealerDetails;
import com.filipmajewski.jeggerweb.entity.Dealer;
import com.filipmajewski.jeggerweb.entity.DealerHandlowcy;
import com.filipmajewski.jeggerweb.repository.DealerHandlowcyRepository;
import com.filipmajewski.jeggerweb.repository.DealerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class DealersController {

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private DealerHandlowcyRepository dealerHandlowcyRepository;

    @RequestMapping(value = "/dealerzy/new/add", method = RequestMethod.POST)
    public ModelAndView addDealer(@RequestParam("name") String name,
                                  @RequestParam("branch") String branch,
                                  @RequestParam("nip") long nip,
                                  @RequestParam("regon") long regon,
                                  @RequestParam("street") String street,
                                  @RequestParam("postcode") Integer postcode,
                                  @RequestParam("city") String city) {

        ModelAndView model = new ModelAndView("redirect:/dealerzy");
        Dealer dealer = dealerRepository.findByNip(nip);
        //Add try catch to control existing dealer

        if(name.length() > 128 || branch.length() > 128 || street.length() > 64 || city.length() > 64) {

            //Add error handler

            return model;
        }

        dealerRepository.save(new Dealer(name, branch, nip, regon, street, postcode, city));

        //Add message to model

        return model;
    }

    @RequestMapping(value = "/dealerzy/details", method = RequestMethod.GET)
    public ModelAndView dealerDetails(@RequestParam int id) {

        ModelAndView mv = new ModelAndView();

        try{

            Dealer dealer = dealerRepository.findById(id).orElse(new Dealer());

            List<DealerHandlowcy> handlowcyDealera = dealerHandlowcyRepository.findAllByDealerID(dealer.getId());

            mv.addObject("dealerDetails", new DealerDetails(dealer, handlowcyDealera));

            mv.setViewName("dealers_details.html");

        } catch (NullPointerException e) {
            mv.setViewName("redirect:/dealerzy");
        }

        return mv;
    }

    @RequestMapping(value = "/dealerzy/modify", method = RequestMethod.GET)
    public ModelAndView modifyDealerPage(@RequestParam int id) {

        ModelAndView mv = new ModelAndView();

        Dealer dealer = dealerRepository.findById(id).orElse(new Dealer());

        if(dealer.getCompany() != null) {

            mv.addObject("dealer", dealer);
            mv.setViewName("dealers_modify.html");

        } else {
            //Add error handler
            mv.setViewName("redirect:/dealerzy");
        }

        return mv;
    }

    @RequestMapping(value = "/dealerzy/modify/submit", method = RequestMethod.POST)
    public ModelAndView modifyDealer(@RequestParam("company") String company,
                                     @RequestParam("branch") String branch,
                                     @RequestParam("nip") long nip,
                                     @RequestParam("regon") long regon,
                                     @RequestParam("street") String street,
                                     @RequestParam("postcode") int postcode,
                                     @RequestParam("city") String city,
                                     @RequestParam("id") int id) {

        ModelAndView mv = new ModelAndView("redirect:/dealerzy");

        if(company.length() > 128 || branch.length() > 128 || street.length() > 64 || city.length() > 64) {

            //Add error handler
            return mv;

        }

        Dealer dealer = dealerRepository.findById(id).orElse(new Dealer());

        if(dealer.getCompany() != null) {

            dealer.setCompany(company);
            dealer.setBranch(branch);
            dealer.setNip(nip);
            dealer.setRegon(regon);
            dealer.setStreet(street);
            dealer.setPostcode(postcode);
            dealer.setCity(city);

            dealerRepository.save(dealer);

            return mv;

        } else {
            //Add error handler
            return mv;
        }
    }

}
