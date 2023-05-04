IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.GL_ACCOUNT_AND_MEASURE_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW GL_ACCOUNT_AND_MEASURE_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.MEASURE_AND_PRODUCT_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW MEASURE_AND_PRODUCT_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.PARTY_ROLE_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW PARTY_ROLE_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_ASSOC_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_ASSOC_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_TRANS_ALL_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_TRANS_ALL_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_TRANS_NOOBJ_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_TRANS_NOOBJ_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_TRANS_OBJ_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_TRANS_OBJ_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_TRANS_INDIC_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_TRANS_INDIC_VIEW
    END

IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_TRANS_UO_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_TRANS_UO_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.ACCTG_TRANS_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW ACCTG_TRANS_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_NOTE_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_NOTE_VIEW
    END 
        
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_VIEW
    END 
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_VIEW_ASS')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_VIEW_ASS
    END     
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.PARTY_HISTORY_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW PARTY_HISTORY_VIEW
    END
    
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.WORK_EFFORT_VIEW_REF')
                    AND type = 'V') 
    BEGIN
        DROP VIEW WORK_EFFORT_VIEW_REF
    END    
	
IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.GL_ACCOUNT_WITH_WORK_EFFORT_PURPOSE_TYPE_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW GL_ACCOUNT_WITH_WORK_EFFORT_PURPOSE_TYPE_VIEW
    END 

IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.OBO_RESP_POL')
                    AND type = 'V') 
    BEGIN
        DROP VIEW OBO_RESP_POL
    END

IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.SCORE_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW SCORE_VIEW
    END

IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.REND_PIAO_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW REND_PIAO_VIEW
    END

IF EXISTS (SELECT * FROM sys.objects
            WHERE object_id = OBJECT_ID('dbo.PERF_INDIC_VIEW')
                    AND type = 'V') 
    BEGIN
        DROP VIEW PERF_INDIC_VIEW
    END	
	