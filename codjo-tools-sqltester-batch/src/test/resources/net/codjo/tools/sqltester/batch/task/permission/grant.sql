Print "Début Création des autorisations"
go
grant select, insert, delete, update, references on PM_PUBLIC_COLUMN_LABEL to Maintenance,Administration,Batch
go
grant select, insert, delete, update, references on PM_TABLE_LABEL to Maintenance,Administration,Batch
go
grant select, insert, delete, update, references on AP_LOG to Maintenance,Administration,Batch
go
grant select, insert, delete, update, references on AP_LOG2 to Maintenance,Administration,Batch
go

grant select on VU_ACTION_EXPENSE to Utilisateur,Maintenance,Administration,Batch,Coordinateur,Consultation
go
grant select on VU_ACCOUNTING_ENTRY_CONSO to Utilisateur,Maintenance,Administration,Batch,Coordinateur,Consultation
go

grant execute on sp_GABI_Sel_CakeShare to Utilisateur,Maintenance,Administration,Batch,Coordinateur,Consultation
go
grant execute on sp_GABI_Sel_ActionIndicators to Utilisateur,Maintenance,Administration,Batch,Coordinateur,Consultation
go
Print "Fin Création des autorisations"
go
