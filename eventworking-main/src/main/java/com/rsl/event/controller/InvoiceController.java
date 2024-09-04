package com.rsl.event.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rsl.event.entity.Invoice;
import com.rsl.event.service.InvoiceService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;

    // Fetch all invoices from the database
    @GetMapping("/invoice")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        logger.trace("Entering getAllInvoices method.");
        List<Invoice> list = invoiceService.getAllInvoiceList();

        if (list.isEmpty()) {
            logger.info("No invoices found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if no invoices are found
        }
        logger.info("Invoices retrieved successfully.");
        return ResponseEntity.ok(list); // Return 200 with the list of invoices
    }

    // Add a new invoice to the database
    @PostMapping("/invoice")
    public ResponseEntity<Invoice> addInvoice(@Valid @RequestBody Invoice invoice) {
        logger.trace("Entering addInvoice method.");
        try {
            Invoice addedInvoice = invoiceService.addInvoice(invoice);
            logger.info("Invoice added successfully with ID: {}", addedInvoice.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(addedInvoice); // Return 201 with the created invoice
        } catch (Exception e) {
            logger.error("Error adding invoice: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 if an error occurs
        }
    }

    // Retrieve an invoice by its ID
    @GetMapping("/invoice/{incId}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable("incId") int incId) {
        logger.trace("Entering getInvoice method with ID: {}", incId);
        Optional<Invoice> invoiceOpt = invoiceService.getInvoiceById(incId);

        return invoiceOpt
                .map(invoice -> {
                    logger.info("Invoice with ID: {} retrieved successfully.", incId);
                    return ResponseEntity.ok(invoice); // Return 200 with the invoice
                })
                .orElseGet(() -> {
                    logger.warn("Invoice with ID: {} not found.", incId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if invoice is not found
                });
    }

    // Delete an invoice by its ID
    @DeleteMapping("/invoice/{incId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable("incId") int incId) {
        logger.trace("Entering deleteInvoice method with ID: {}", incId);
        try {
            invoiceService.deleteInvoice(incId);
            logger.info("Invoice with ID: {} deleted successfully.", incId);
            return ResponseEntity.noContent().build(); // Return 204 if deletion is successful
        } catch (Exception e) {
            logger.error("Error deleting invoice with ID: {}", incId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 if an error occurs
        }
    }

    // Update an existing invoice
    @PutMapping("/invoice/{incId}")
    public ResponseEntity<Invoice> updateInvoice(@RequestBody Invoice invoice, @PathVariable("incId") int incId) {
        logger.trace("Entering updateInvoice method with ID: {}", incId);
        try {
            Invoice updatedInvoice = invoiceService.updateInvoice(invoice, incId);
            logger.info("Invoice with ID: {} updated successfully.", incId);
            return ResponseEntity.ok(updatedInvoice); // Return 200 with the updated invoice
        } catch (Exception e) {
            logger.error("Error updating invoice with ID: {}", incId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 if an error occurs
        }
    }
}
