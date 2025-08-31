-- Add ON DELETE CASCADE constraints to expense and income tables

-- First, drop existing foreign key constraints
ALTER TABLE tbl_expense DROP FOREIGN KEY IF EXISTS FK_expense_profile;
ALTER TABLE tbl_incomes DROP FOREIGN KEY IF EXISTS FK_income_profile;

-- Re-add foreign key constraints with ON DELETE CASCADE
ALTER TABLE tbl_expense ADD CONSTRAINT FK_expense_profile
    FOREIGN KEY (profile_id) REFERENCES tbl_profiles(id) ON DELETE CASCADE;
    
ALTER TABLE tbl_incomes ADD CONSTRAINT FK_income_profile
    FOREIGN KEY (profile_id) REFERENCES tbl_profiles(id) ON DELETE CASCADE;