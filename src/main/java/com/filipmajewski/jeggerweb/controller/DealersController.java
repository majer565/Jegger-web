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
                                  @RequestParam("nip") String nip,
                                  @RequestParam("regon") String regon,
                                  @RequestParam("street") String street,
                                  @RequestParam("postcode") Integer postcode,
                                  @RequestParam("city") String city) {

        ModelAndView model = new ModelAndView("redirect:/dealerzy/new");

        //Add error handler


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

}
