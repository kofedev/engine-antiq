package dev.kofe.kengine.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validator")
public class DomainValidationController {

    @GetMapping("/domain")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public boolean checkIfDomainExists(@RequestParam(name = "domain") String domain) {
        try {
            InetAddress.getByName(domain);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

}
