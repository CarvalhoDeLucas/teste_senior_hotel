ALTER TABLE guest ADD CONSTRAINT uk_guest_document UNIQUE (document);
ALTER TABLE guest ADD CONSTRAINT uk_guest_phone UNIQUE (phone);
