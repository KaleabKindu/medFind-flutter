package com.gis.medfind.restController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import com.gis.medfind.Forms.PillForm;
import com.gis.medfind.entity.MedPack;
import com.gis.medfind.entity.Pharmacy;
import com.gis.medfind.entity.Pill;
import com.gis.medfind.entity.User;
import com.gis.medfind.jwt.JwtTokenUtil;
import com.gis.medfind.repository.MedicineRepository;
import com.gis.medfind.repository.UserRepository;
import com.gis.medfind.service.WatchListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(consumes = "application/json",produces = "application/json")
public class WatchListController {
    @Autowired
    UserRepository userRepo;

    @Autowired
    WatchListService  watchListServ;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    MedicineRepository medicineRepo;

    @RequestMapping(value="/api/v1/watchlist/search",method = RequestMethod.POST)
    public ResponseEntity<?> searchMedPack(@RequestParam(name = "medpack_id") String medpack_id,@RequestBody Map<String,String> location){
        try{
          List<Pharmacy> pharmacies =   watchListServ.findMedicinesCloseToUser(Long.parseLong(medpack_id), Double.parseDouble(location.get("userlat")), Double.parseDouble(location.get("userlong")));

          List<Map<String,String>> sizedPharm  = new ArrayList<>();

        
           pharmacies.forEach(
            i->{
                Map<String,String>  phar = new HashMap<>();
                phar.put("id", i.getId().toString());
                phar.put("name",i.getName());
                phar.put("address",i.getAddress());
                sizedPharm.add(phar);
            }
        );
          return new ResponseEntity<List<Map<String,String>>>(sizedPharm, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<String>("searchFailed ", HttpStatus.BAD_REQUEST);
        }
        
    }

    @RequestMapping(value = "/api/v1/watchlist",method =RequestMethod.GET)
    public ResponseEntity<?> watchList(@RequestHeader(name ="Authorization") String token){
        try{
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            User user = userRepo.findByEmail(email);
            
            List<MedPack> medpacks =  watchListServ.getMedpacksFromWatchlist(user.getId());
            Map<String,Object> resp = new HashMap<>();
            List<String> medicines = new ArrayList<>();
            medicineRepo.findAll().forEach(i->{
                medicines.add(i.getName());
            });
            resp.put("medicines",medicines);
            resp.put("medpacks",medpacks);
            
            return new ResponseEntity<Map<String,Object>>(resp,HttpStatus.OK);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<String>("user not found ", HttpStatus.NOT_FOUND);
        }
    }

    
    @RequestMapping(value = "/api/v1/watchlist/medpacks",method = RequestMethod.POST)
    public ResponseEntity<?> addMedpack(@RequestBody Map<String,String> tag,@RequestHeader(name ="Authorization") String token ){
        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        User user = userRepo.findByEmail(email);

        MedPack medPack =  watchListServ.createMedpackInWatchlist(user.getId(),tag.get("tag"));
        return new ResponseEntity<MedPack>(medPack,HttpStatus.OK);
    }


    @RequestMapping(value = "/api/v1/watchlist/medpacks",method = RequestMethod.PUT)
    public ResponseEntity<?> updateMedpackTag(@RequestParam(name="id") String id,@RequestBody Map<String,String> tag){
        MedPack medpack =watchListServ.editMedpackTag(Long.parseLong(id),tag.get("tag"));
        return new ResponseEntity<MedPack>(medpack,HttpStatus.OK);
    }


    @RequestMapping(value = "/api/v1/watchlist/medpacks",method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMedpack(@RequestParam(name="id") String id,@RequestHeader(name ="Authorization") String token){
        try{
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            User user = userRepo.findByEmail(email);


            if (watchListServ.deleteMedpack(user.getId(),Long.parseLong(id))){
                return new ResponseEntity<String >("Delete success",HttpStatus.ACCEPTED);
            }

            return new ResponseEntity<String >("Delete failed",HttpStatus.NOT_ACCEPTABLE);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<String>("user not found", HttpStatus.NOT_FOUND);
        }
        
    }

   

    @RequestMapping(value="/api/v1/watchlist/medpacks/pills",method = RequestMethod.POST)
    public ResponseEntity<?> addPill(@RequestParam(name="medpack_id") String medpack_id, @RequestBody PillForm pillForm){
       Pill pill =  watchListServ.addPillToMedpack(Long.parseLong(medpack_id), pillForm.getMedicine_name(),pillForm.getStrength(),pillForm.getAmount());
        return new ResponseEntity<Pill>(pill, HttpStatus.OK);

    }


    @RequestMapping(value="/api/v1/watchlist/medpacks/pills",method = RequestMethod.PUT)
    public ResponseEntity<?> updatePill(@RequestParam(name="pill_id") String pill_id, @RequestBody PillForm pillForm){
        Pill pill = watchListServ.updatePillAmount(Long.parseLong(pill_id),pillForm.getAmount());
        pill = watchListServ.updatePillStrength( Long.parseLong(pill_id),pillForm.getStrength());
        return new ResponseEntity<Pill>(pill, HttpStatus.OK);
    }


    @RequestMapping(value="/api/v1/watchlist/medpacks/pills",method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePill(@RequestParam(name="medpack_id") String medpack_id, @RequestParam(name = "pill_id") String pill_id){
        System.out.println(medpack_id + " "+ pill_id);
        if (watchListServ.removePillFromMedpack(Long.parseLong(pill_id), Long.parseLong(medpack_id)))
            return new ResponseEntity<String>("Deletion successful", HttpStatus.OK);
        return new ResponseEntity<String>("pill not found", HttpStatus.NOT_FOUND);
    }
    
}