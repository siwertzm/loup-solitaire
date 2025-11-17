import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CombatPage } from './combat.page';

describe('CombatPage', () => {
  let component: CombatPage;
  let fixture: ComponentFixture<CombatPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(CombatPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
