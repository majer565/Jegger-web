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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                  @RequestParam("city") String city,
                                  RedirectAttributes rdir) {

        ModelAndView model = new ModelAndView("redirect:/dealerzy");

        Dealer dealer = dealerRepository.findByNip(nip);

        if(dealer != null) {

            rdir.addFlashAttribute("dealerError", "Dealer o podanym numerze NIP już istnieje.");
            return model;

        } else if(name.length() > 128) {

            rdir.addFlashAttribute("dealerError", "Błąd przy dodawaniu dealera. Za długa nazwa dealera.");
            return model;

        } else if(branch.length() > 128) {

            rdir.addFlashAttribute("dealerError", "Błąd przy dodawaniu dealera. Za długa nazwa oddziału.");
            return model;

        } else if(street.length() > 128) {

            rdir.addFlashAttribute("dealerError", "Błąd przy dodawaniu dealera. Za długa nazwa ulicy.");
            return model;

        } else if(city.length() > 128) {

            rdir.addFlashAttribute("dealerError", "Błąd przy dodawaniu dealera. Za długa nazwa miasta");
            return model;

        }

        dealerRepository.save(new Dealer(name, branch, nip, regon, street, postcode, city));
        rdir.addFlashAttribute("dealerSuccess", "Pomyślnie dodano nowego dealera.");

        return model;
    }

    @RequestMapping(value = "/dealerzy/details", method = RequestMethod.GET)
    public ModelAndView dealerDetails(@RequestParam int id, RedirectAttributes redir) {

        ModelAndView mv = new ModelAndView();

        Dealer dealer = dealerRepository.findById(id).orElse(new Dealer());

        if(dealer.getNip() == 0) {
            mv.setViewName("redirect:/dealerzy");
            redir.addFlashAttribute("dealerError", "Błąd. Nie znaleziono dealera o takim numerze ID.");
        } else {
            List<DealerHandlowcy> handlowcyDealera = dealerHandlowcyRepository.findAllByDealerID(dealer.getId());

            mv.addObject("dealerDetails", new DealerDetails(dealer, handlowcyDealera));

            mv.setViewName("dealers_details.html");
        }

        return mv;
    }

    @RequestMapping(value = "/dealerzy/modify", method = RequestMethod.GET)
    public ModelAndView modifyDealerPage(@RequestParam int id, RedirectAttributes redir) {

        ModelAndView mv = new ModelAndView();

        Dealer dealer = dealerRepository.findById(id).orElse(new Dealer());

        if(dealer.getCompany() != null) {

            mv.addObject("dealer", dealer);
            mv.setViewName("dealers_modify.html");

        } else {
            redir.addFlashAttribute("dealerError", "Błąd. Nie znaleziono dealera o takim numerze ID.");
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
                                     @RequestParam("id") int id,
                                     RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/dealerzy");

        if(company.length() > 128) {

            rdir.addFlashAttribute("dealerError", "Błąd przy dodawaniu dealera. Za długa nazwa dealera.");
            return mv;

        } else if(branch.length() > 128) {

            rdir.addFlashAttribute("dealerError", "Błąd przy dodawaniu dealera. Za długa nazwa oddziału.");
            return mv;

        } else if(street.length() > 128) {

            rdir.addFlashAttribute("dealerError", "Błąd przy dodawaniu dealera. Za długa nazwa ulicy.");
            return mv;

        } else if(city.length() > 128) {

            rdir.addFlashAttribute("dealerError", "Błąd przy dodawaniu dealera. Za długa nazwa miasta");
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
            rdir.addFlashAttribute("dealerError", "Błąd. Nie znaleziono dealera.");
            return mv;
        }
    }

}
