import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ChapitrePage } from './chapitre.page';

describe('ChapitrePage', () => {
  let component: ChapitrePage;
  let fixture: ComponentFixture<ChapitrePage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ChapitrePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
