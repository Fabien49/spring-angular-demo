import {Component, OnInit} from '@angular/core';
import {StudentsService} from "../services/students.service";
import {ActivatedRoute} from "@angular/router";
import {Payment} from "../model/students.model";

@Component({
  selector: 'app-payment-details',
  templateUrl: './payment-details.component.html',
  styleUrl: './payment-details.component.css'
})
export class PaymentDetailsComponent implements OnInit{

  paymentId! : number;
  pdfFileUrl! : any;
  payment! : Payment;

  constructor(private studentsService : StudentsService, private route : ActivatedRoute) {
  }

  ngOnInit() {
    this.paymentId = this.route.snapshot.params['id'];
    this.studentsService.getPaymentDetails(this.paymentId).subscribe({
      next : value => {
        let blob = new Blob([value], {type: 'application/pdf'});
        this.pdfFileUrl = window.URL.createObjectURL(blob)
      },
      error : err => {
        console.log(err)
      }
    });
  }

  afterLoadComplete($event: any) {

  }
}
